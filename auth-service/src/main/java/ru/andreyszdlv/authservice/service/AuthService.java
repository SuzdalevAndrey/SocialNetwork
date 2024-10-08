package ru.andreyszdlv.authservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.dto.controllerDto.ConfirmEmailRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerDto.LoginRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerDto.LoginResponseDTO;
import ru.andreyszdlv.authservice.dto.controllerDto.RefreshTokenRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerDto.RefreshTokenResponseDTO;
import ru.andreyszdlv.authservice.dto.controllerDto.RegisterRequestDTO;
import ru.andreyszdlv.authservice.enums.ERole;
import ru.andreyszdlv.authservice.exception.RegisterUserNotFoundException;
import ru.andreyszdlv.authservice.exception.ValidateTokenException;
import ru.andreyszdlv.authservice.exception.VerificationTokenNotSuitableException;
import ru.andreyszdlv.authservice.model.PendingUser;
import ru.andreyszdlv.authservice.model.User;
import ru.andreyszdlv.authservice.repository.PendingUserRepo;
import ru.andreyszdlv.authservice.repository.UserRepo;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {
    private final EmailVerificationService emailVerificationService;

    private final UserRepo userRepository;

    private final PendingUserRepo pendingUserRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtSecurityService jwtSecurityService;

    private final KafkaProducerService kafkaProducerService;

    private final AuthenticationManager authenticationManager;

    @Transactional
    public void registerUser(RegisterRequestDTO request) {
        log.info("Executing registerUser method in AuthService for email: {} and name: {}",
                request.email(),
                request.name());

        log.info("Checking whether the user is registered with email: {}", request.email());
        if(pendingUserRepo.existsByEmail(request.email())
                || userRepository.existsByEmail(request.email())) {
            log.error("The user is already registered with email: {}", request.email());
            throw new RegisterUserNotFoundException("errors.409.user_already_register");
        }

        PendingUser user = new PendingUser();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(ERole.USER);
        user.setCreatedAt(LocalDateTime.now());

        log.info("Verification code generation");
        String verificationCode = emailVerificationService
                .generateAndSaveVerificationCode(request.email());

        log.info("Sending a message to kafka that contains userEmail: {}", user.getEmail());
        kafkaProducerService.sendRegisterEvent(user.getEmail(), verificationCode);

        log.info("Saving a user to a temporary database");
        pendingUserRepo.save(user);
    }

    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                ));

        log.info("Executing loginUser method in AuthService for email: {}", request.email());

        log.info("Verification of the user existence with email: {}", request.email());
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(()->new UsernameNotFoundException("errors.404.user_not_found"));

        log.info("Token generation for userEmail: {}", user.getEmail());
        String token = jwtSecurityService.generateToken(user, user.getRole().name());

        log.info("RefreshToken generation for userEmail: {}", user.getEmail());
        String refreshToken = jwtSecurityService.generateRefreshToken(user);

        log.info("LoginUser completed successfully with email: {}", request.email());

        kafkaProducerService.sendLoginEvent(user.getName(), user.getEmail());

        return new LoginResponseDTO(user.getEmail(), token, refreshToken);
    }

    public RefreshTokenResponseDTO refresh(RefreshTokenRequestDTO request) {
        log.info("Executing refresh method in AuthService");

        String refreshToken = request.refreshToken();

        String email = jwtSecurityService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("errors.404.user_not_found"));

        if(jwtSecurityService.validateToken(refreshToken, user)) {
            log.info("Refresh token completed successfully");

            return new RefreshTokenResponseDTO(
                    jwtSecurityService.generateToken(user, user.getRole().name()),
                    jwtSecurityService.generateRefreshToken(user)
            );
        }

        log.error("Validate token errors");

        throw new ValidateTokenException("errors.409.is_not_valid_token");
    }

    @Transactional
    public void confirmEmail(ConfirmEmailRequestDTO request){
        log.info("Executing confirmEmail method in AuthService for email: {}", request.email());

        log.info("Getting the access code for email: {}", request.email());


        log.info("Getting a user from a table pendings user by email: {}", request.email());
        PendingUser pendingUser = pendingUserRepo
                .findByEmail(request.email())
                .orElseThrow(
                        ()->new NoSuchElementException("errors.404.email_not_found")
                );

        log.info("Comparison verification code with code sent by user");
        if(emailVerificationService.isValidCode(request.email(), request.code())){
            log.info("The code from user and correct code match");
            User user = new User();

            user.setName(pendingUser.getName());
            user.setEmail(pendingUser.getEmail());
            user.setPassword(pendingUser.getPassword());
            user.setRole(pendingUser.getRole());

            log.info("Saving the user to a permanent database");
            userRepository.save(user);

            return;
        }
        log.error("The verification code and the code sent by the user do not match");
        throw new VerificationTokenNotSuitableException("errors.409.verification_token_is_not_valid");
    }

    @Transactional
    public void updateVerificationCode(String userEmail) {
        log.info("Executing updateVerificationCode method in AuthService for email: {}", userEmail);

        if(!pendingUserRepo.existsByEmail(userEmail)
                || userRepository.existsByEmail(userEmail)) {
            log.error("The user is already registered with email: {}", userEmail);
            throw new RegisterUserNotFoundException("errors.409.user_already_register");
        }

        log.info("Verification code generation");
        String verificationCode = emailVerificationService
                .generateAndSaveVerificationCode(userEmail);

        log.info("Sending a message to kafka that contains userEmail: {}", userEmail);
        kafkaProducerService.sendRegisterEvent(userEmail, verificationCode);
    }
}
