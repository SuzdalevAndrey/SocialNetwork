package ru.andreyszdlv.authservice.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.authservice.controller.dto.LoginRequestDTO;
import ru.andreyszdlv.authservice.controller.dto.LoginResponseDTO;
import ru.andreyszdlv.authservice.controller.dto.RefreshTokenRequestDTO;
import ru.andreyszdlv.authservice.controller.dto.RefreshTokenResponseDTO;
import ru.andreyszdlv.authservice.controller.dto.RegisterRequestDTO;
import ru.andreyszdlv.authservice.enums.ERole;
import ru.andreyszdlv.authservice.exception.ValidateTokenException;
import ru.andreyszdlv.authservice.model.User;
import ru.andreyszdlv.authservice.repository.UserRepo;

@Service
@AllArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepo userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtSecurityService jwtSecurityService;

    public User registerUser(RegisterRequestDTO request) {
        log.info("Executing registerUser method in AuthService for email: {} and name: {}",
                request.email(),
                request.name());

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(ERole.USER);

        log.info("RegisterUser completed successfully with  email: {} and name: {}",
                request.email(),
                request.name());
        return userRepository.save(user);
    }

    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        log.info("Executing loginUser method in AuthService for email: {}", request.email());

        log.info("Verification of the user existence with email: {}", request.email());
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(()->new UsernameNotFoundException("errors.404.user_not_found"));

        log.info("Token generation for userEmail: {}", user.getEmail());
        String token = jwtSecurityService.generateToken(user, user.getRole().name());

        log.info("RefreshToken generation for userEmail: {}", user.getEmail());
        String refreshToken = jwtSecurityService.generateRefreshToken(user);

        log.info("LoginUser completed successfully with email: {}", request.email());

        return new LoginResponseDTO(user.getEmail(), token, refreshToken);
    }

    public RefreshTokenResponseDTO refresh(RefreshTokenRequestDTO request) {
        log.info("Executing refresh method in AuthService for email");

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

}
