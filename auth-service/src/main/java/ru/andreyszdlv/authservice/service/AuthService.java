package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.dto.controller.LoginRequestDTO;
import ru.andreyszdlv.authservice.dto.controller.LoginResponseDTO;
import ru.andreyszdlv.authservice.dto.controller.RefreshTokenRequestDTO;
import ru.andreyszdlv.authservice.dto.controller.RefreshTokenResponseDTO;
import ru.andreyszdlv.authservice.exception.ValidateTokenException;
import ru.andreyszdlv.authservice.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KafkaProducerService kafkaProducerService;

    private final AuthenticationManager authenticationManager;

    private final AccessAndRefreshJwtService accessAndRefreshJwtService;

    private final JwtSecurityService jwtSecurityService;

    @Transactional
    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        log.info("Executing loginUser in AuthService for email: {}", request.email());

        log.info("Checking login user with email: {}", request.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                ));
        User user = (User) authentication.getPrincipal();

        log.info("Token generation for user: {}", user.getId());
        String token = accessAndRefreshJwtService
                .generateAccessToken(user.getId(), user.getRole().name());

        log.info("RefreshToken generation for user: {}", user.getId());
        String refreshToken = accessAndRefreshJwtService
                .generateRefreshToken(user.getId(), user.getRole().name());

        log.info("Login completed successfully with id: {}", user.getId());
        kafkaProducerService.sendLoginEvent(user.getName(), user.getEmail());

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

        String expectedRefreshToken = accessAndRefreshJwtService.getRefreshTokenByUserId(userId);

        log.info("Validate token");
        if(jwtSecurityService.validateToken(token)
                && expectedRefreshToken != null
                && expectedRefreshToken.equals(token)
        ){
            log.info("Successfully validated token");

            log.info("Generate and save refresh and access token");
            String accessToken = accessAndRefreshJwtService.generateAccessToken(userId, role);
            String refreshToken = accessAndRefreshJwtService.generateRefreshToken(userId, role);

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