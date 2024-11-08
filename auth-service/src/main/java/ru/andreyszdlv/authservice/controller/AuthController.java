package ru.andreyszdlv.authservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.authservice.dto.controller.ConfirmEmailRequestDTO;
import ru.andreyszdlv.authservice.dto.controller.LoginRequestDTO;
import ru.andreyszdlv.authservice.dto.controller.LoginResponseDTO;
import ru.andreyszdlv.authservice.dto.controller.RefreshTokenRequestDTO;
import ru.andreyszdlv.authservice.dto.controller.RefreshTokenResponseDTO;
import ru.andreyszdlv.authservice.dto.controller.RegisterRequestDTO;
import ru.andreyszdlv.authservice.dto.controller.UpdateVerifiCodeRequestDTO;
import ru.andreyszdlv.authservice.service.AuthService;
import ru.andreyszdlv.authservice.service.LocalizationService;

import java.util.Locale;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                                                  BindingResult bindingResult)
            throws BindException {
        log.info("Login user for email: {} ", request.email());

        if(bindingResult.hasErrors()) {
            log.error("Validation errors during login user: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            log.info("Validation successful, login user with email: {}", request.email());

            LoginResponseDTO response = authService.loginUser(request);

            log.info("User login completed successfully with  email: {}", request.email());

            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request,
                                                           BindingResult bindingResult)
            throws BindException{
        log.info("Executing refresh method in AuthController");

        if(bindingResult.hasErrors()) {
            log.error("Validation errors during refresh token: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            log.info("Validation successful, refreshing token");

            RefreshTokenResponseDTO response = authService.refresh(request);

            log.info("Refresh token completed successfully");

            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-User-Id") long userId){
        authService.logout(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
