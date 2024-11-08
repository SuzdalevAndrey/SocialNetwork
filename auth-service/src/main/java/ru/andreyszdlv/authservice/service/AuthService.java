package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.client.UserServiceClient;
import ru.andreyszdlv.authservice.dto.controller.LoginRequestDTO;
import ru.andreyszdlv.authservice.dto.controller.LoginResponseDTO;
import ru.andreyszdlv.authservice.dto.controller.RefreshTokenRequestDTO;
import ru.andreyszdlv.authservice.dto.controller.RefreshTokenResponseDTO;
import ru.andreyszdlv.authservice.dto.client.UserResponseDTO;
import ru.andreyszdlv.authservice.exception.ValidateTokenException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserServiceClient userServiceClient;

    private final JwtSecurityService jwtSecurityService;

    private final KafkaProducerService kafkaProducerService;

    private final AuthenticationManager authenticationManager;

    private final AccessAndRefreshJwtService accessAndRefreshJwtService;

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
        UserResponseDTO user = userServiceClient
                .getUserByEmail(request.email())
                .getBody();

        log.info("Token generation for user: {}", user.id());
        String token = jwtSecurityService.generateToken(
                user.id(),
                user.role().name()
        );

        log.info("RefreshToken generation for user: {}", user.id());
        String refreshToken = jwtSecurityService.generateRefreshToken(
                user.id(),
                user.role().name()
        );

        accessAndRefreshJwtService.saveAccessTokenByUserId(user.id(), token);
        accessAndRefreshJwtService.saveRefreshTokenByUserId(user.id(), refreshToken);

        log.info("Login completed successfully with id: {}", user.id());
        kafkaProducerService.sendLoginEvent(user.name(), user.email());

        return new LoginResponseDTO(token, refreshToken);
    }

    @Transactional
    public RefreshTokenResponseDTO refresh(RefreshTokenRequestDTO request) {
        log.info("Executing refresh in AuthService");

        String token = request.refreshToken();

        long userId = jwtSecurityService.extractUserId(token);
        log.info("Extract userId: {}", userId);

        String role = jwtSecurityService.extractRole(token);
        log.info("Extract role: {}", role);

        log.info("Validate token");
        if(jwtSecurityService.validateToken(token)
                && accessAndRefreshJwtService.getRefreshTokenByUserId(userId) != null
                && accessAndRefreshJwtService.getRefreshTokenByUserId(userId).equals(token)
        ){
            log.info("Successfully validated token");

            log.info("Generate refresh and access token");
            String accessToken = jwtSecurityService.generateToken(userId, role);
            String refreshToken = jwtSecurityService.generateRefreshToken(userId, role);

            log.info("Save tokens to cache");
            accessAndRefreshJwtService.saveAccessTokenByUserId(userId, accessToken);
            accessAndRefreshJwtService.saveRefreshTokenByUserId(userId, refreshToken);

            log.info("Refresh token completed successfully");
            return new RefreshTokenResponseDTO(
                    accessToken,
                    refreshToken
            );
        }

        log.error("Validate token errors");

        throw new ValidateTokenException("errors.409.is_not_valid_token");
    }

    public void logout(long userId) {
        log.info("Executing logout in AuthService");

        accessAndRefreshJwtService.deleteByUserId(userId);
    }
}