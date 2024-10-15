package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.authservice.dto.controllerdto.ConfirmEmailRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerdto.LoginRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerdto.LoginResponseDTO;
import ru.andreyszdlv.authservice.dto.controllerdto.RefreshTokenRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerdto.RefreshTokenResponseDTO;
import ru.andreyszdlv.authservice.dto.controllerdto.RegisterRequestDTO;
import ru.andreyszdlv.authservice.dto.userservicefeigndto.UserResponseDTO;
import ru.andreyszdlv.authservice.exception.UserNeedConfirmException;
import ru.andreyszdlv.authservice.exception.RegisterUserNotFoundException;
import ru.andreyszdlv.authservice.exception.UserAlreadyRegisteredException;
import ru.andreyszdlv.authservice.exception.ValidateTokenException;
import ru.andreyszdlv.authservice.exception.VerificationCodeNotSuitableException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final EmailVerificationService emailVerificationService;

    private final UserServiceFeignClient userServiceFeignClient;

    private final PendingUserService pendingUserService;

    private final JwtSecurityService jwtSecurityService;

    private final KafkaProducerService kafkaProducerService;

    private final AuthenticationManager authenticationManager;

    private final AccessAndRefreshJwtService accessAndRefreshJwtService;

    @Transactional
    public void registerUser(RegisterRequestDTO request) {
        log.info("Executing registerUser in AuthService for email: {} and name: {}",
                request.email(),
                request.name());

        log.info("Checking registered user with email: {}", request.email());
        if(userServiceFeignClient.existsUserByEmail(request.email()).getBody()) {
            log.error("User already registered with email: {}", request.email());
            throw new UserAlreadyRegisteredException("errors.409.user_already_register");
        }

        log.info("Checking user need confirm email: {}", request.email());
        if(pendingUserService.existsUserByEmail(request.email())){
            log.error("User need confirm email: {}", request.email());
            throw new UserNeedConfirmException("errors.409.need_confirm_email");
        }

        log.info("Verification code generation");
        String verificationCode = emailVerificationService
                .generateAndSaveVerificationCode(request.email());

        log.info("Save user to temporary database");
        pendingUserService.savePendingUser(request.name(), request.email(), request.password());

        log.info("Send message to kafka contains userEmail: {} and code", request.email());
        kafkaProducerService.sendRegisterEvent(request.email(), verificationCode);
    }

    @Transactional
    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        log.info("Executing loginUser in AuthService for email: {}", request.email());

        log.info("Checking login user with email: {}", request.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                ));

        log.info("Verification user existence with email: {}", request.email());
        UserResponseDTO user = userServiceFeignClient
                .getUserByEmail(request.email())
                .getBody();

        log.info("Token generation for user: {}", user.email());
        String token = jwtSecurityService.generateToken(user.email(), user.role().name());

        log.info("RefreshToken generation for user: {}", user.email());
        String refreshToken = jwtSecurityService.generateRefreshToken(user.email(), user.role().name());

        accessAndRefreshJwtService.saveAccessTokenByUserEmail(user.email(), token);
        accessAndRefreshJwtService.saveRefreshTokenByUserEmail(user.email(), refreshToken);

        log.info("Login completed successfully with email: {}", request.email());
        kafkaProducerService.sendLoginEvent(user.name(), user.email());

        return new LoginResponseDTO(token, refreshToken);
    }

    @Transactional
    public RefreshTokenResponseDTO refresh(RefreshTokenRequestDTO request) {
        log.info("Executing refresh in AuthService");

        String token = request.refreshToken();

        String email = jwtSecurityService.extractEmail(token);
        log.info("Extract email: {}", email);

        String role = jwtSecurityService.extractRole(token);
        log.info("Extract role: {}", role);

        log.info("Validate token");
        if(jwtSecurityService.validateToken(token)
                && accessAndRefreshJwtService.getRefreshTokenByUserEmail(email) != null
                && accessAndRefreshJwtService.getRefreshTokenByUserEmail(email).equals(token)
        ){
            log.info("Successfully validated token");

            log.info("Generate refresh and access token");
            String accessToken = jwtSecurityService.generateToken(email, role);
            String refreshToken = jwtSecurityService.generateRefreshToken(email, role);

            log.info("Save tokens to cache");
            accessAndRefreshJwtService.saveAccessTokenByUserEmail(email, accessToken);
            accessAndRefreshJwtService.saveRefreshTokenByUserEmail(email, refreshToken);

            log.info("Refresh token completed successfully");
            return new RefreshTokenResponseDTO(
                    accessToken,
                    refreshToken
            );
        }

        log.error("Validate token errors");

        throw new ValidateTokenException("errors.409.is_not_valid_token");
    }

    @Transactional
    public void confirmEmail(ConfirmEmailRequestDTO request){
        log.info("Executing confirmEmail in AuthService for email: {}", request.email());

        log.info("Comparison verification code with code sent by user");
        if(emailVerificationService.isValidCode(request.email(), request.code())){
            log.info("The code from user is valid");

            log.info("Saving the user to a permanent database");
            pendingUserService.savePendingUserInPermanentBD(request.email());

            return;
        }
        log.error("Code sent user with email: {} not valid", request.email());
        throw new VerificationCodeNotSuitableException("errors.409.verification_token_is_not_valid");
    }

    @Transactional
    public void updateVerificationCode(String userEmail) {
        log.info("Executing updateVerificationCode in AuthService for email: {}", userEmail);

        log.info("Checking register user with email: {}", userEmail);
        if(!pendingUserService.existsUserByEmail(userEmail)) {
            log.error("The user is already registered with email: {}", userEmail);
            throw new RegisterUserNotFoundException("errors.404.email_not_found");
        }

        log.info("Verification code generation for email: {}", userEmail);
        String verificationCode = emailVerificationService
                .generateAndSaveVerificationCode(userEmail);

        log.info("Sending message to kafka contains userEmail: {}", userEmail);
        kafkaProducerService.sendRegisterEvent(userEmail, verificationCode);
    }

    public Map<String, String> generateDataUserUsingToken(String token) {
        log.info("Executing generateDataUserUsingToken in AuthService");

        String email = jwtSecurityService.extractEmail(token);
        log.info("Extract email: {}", email);

        log.info("Validate token");
        if(jwtSecurityService.validateToken(token)
                && accessAndRefreshJwtService.getAccessTokenByUserEmail(email) != null
                && accessAndRefreshJwtService.getAccessTokenByUserEmail(email).equals(token)
        ){
            log.info("Token is valid");

            log.info("Generate data user using token");

            HashMap<String, String> dataUser = new HashMap<>(2);

            dataUser.put("email", email);

            String role = jwtSecurityService.extractRole(token);
            log.info("Extract role: {}", role);
            dataUser.put("role", role);

            return dataUser;
        }
        throw new ValidateTokenException("errors.409.is_not_valid_token");
    }

    public void logout(String email) {
        log.info("Executing logout in AuthService");

        accessAndRefreshJwtService.deleteByUserEmail(email);
    }
}