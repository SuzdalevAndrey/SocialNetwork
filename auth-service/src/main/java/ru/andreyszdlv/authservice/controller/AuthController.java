package ru.andreyszdlv.authservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.authservice.controller.dto.LoginRequestDTO;
import ru.andreyszdlv.authservice.controller.dto.LoginResponseDTO;
import ru.andreyszdlv.authservice.controller.dto.RefreshTokenRequestDTO;
import ru.andreyszdlv.authservice.controller.dto.RefreshTokenResponseDTO;
import ru.andreyszdlv.authservice.controller.dto.RegisterRequestDTO;
import ru.andreyszdlv.authservice.controller.dto.RegisterResponseDTO;
import ru.andreyszdlv.authservice.model.User;
import ru.andreyszdlv.authservice.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request,
                                                        BindingResult bindingResult)
            throws BindException {

        log.info("Executing register method in AuthController for email: {} and name: {}",
                request.email(),
                request.name());

        if(bindingResult.hasErrors()) {
            log.error("Validation errors occurred during register user: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            log.info("Validation successful, register user with email: {} and name: {}",
                    request.email(),
                    request.name());

            User user = authService.registerUser(request);

            log.info("User register completed successfully with  email: {} and name: {}",
                    request.email(),
                    request.name());
            return ResponseEntity.ok(new RegisterResponseDTO(user.getName(), user.getEmail()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                                                  BindingResult bindingResult)
            throws BindException {
        log.info("Executing login method in AuthController for email: {} ", request.email());

        if(bindingResult.hasErrors()) {
            log.error("Validation errors occurred during login user: {}",
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
            log.error("Validation errors occurred during refresh token: {}",
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
}
