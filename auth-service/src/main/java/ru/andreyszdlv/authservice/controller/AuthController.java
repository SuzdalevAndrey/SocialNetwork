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

    private final LocalizationService localizationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO request,
                                           BindingResult bindingResult,
                                           Locale locale)
            throws BindException {

        log.info("Register user for email: {} and name: {}",
                request.email(),
                request.name());

        if(bindingResult.hasErrors()) {
            log.error("Validation errors during register user: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            log.info("Validation successful, register user with email: {} and name: {}",
                    request.email(),
                    request.name());

            authService.registerUser(request);

            log.info("User register completed successfully with  email: {} and name: {}",
                    request.email(),
                    request.name());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            localizationService.getLocalizedMessage(
                                "message.ok.register",
                                locale
                            )
                    );
        }
    }

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

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmEmail(
            @Valid @RequestBody ConfirmEmailRequestDTO request,
            BindingResult bindingResult,
            Locale locale) throws BindException {

        log.info("Executing confirmEmail method in AuthController");

        if(bindingResult.hasErrors()){
            log.error("Validation errors during confirm email: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else{
            log.info("Validation successful, confirm email");

            authService.confirmEmail(request);

            log.info("Confirm email completed successfully");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            localizationService.getLocalizedMessage(
                                "message.ok.confirm_email",
                                locale
                            )
                    );
        }

    }

    @PatchMapping("/update-verification-code")
    public ResponseEntity<String> updatingVerificationCode(
            @Valid @RequestBody UpdateVerifiCodeRequestDTO request,
            BindingResult bindingResult,
            Locale locale)
            throws BindException {

        log.info("Executing updatingVerificationCode method in AuthController");

        if(bindingResult.hasErrors()){
            log.error("Validation errors during update verification code: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            log.info("Validation successful, update verification code");

            authService.updateVerificationCode(request.email());

            log.info("Confirm update verification code successfully");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            localizationService.getLocalizedMessage(
                                "message.ok.update_code",
                                locale
                            )
                    );
        }
    }

    @PostMapping("/generate-data-user")
    public ResponseEntity<Map<String, String>> generateDataUserUsingToken(
            @RequestHeader("Authorization") String token){
        log.info("Executing generateDataUserUsingToken with AuthController");
        Map<String, String> dataUser = authService.generateDataUserUsingToken(token);

        log.info("Confirm generate data user using token successfully");
        return ResponseEntity.ok(dataUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-User-Id") long userId){
        authService.logout(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello!");
    }
}
