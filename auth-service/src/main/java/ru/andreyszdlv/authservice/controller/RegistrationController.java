package ru.andreyszdlv.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.authservice.dto.controller.ConfirmEmailRequestDTO;
import ru.andreyszdlv.authservice.dto.controller.RegisterRequestDTO;
import ru.andreyszdlv.authservice.service.LocalizationService;
import ru.andreyszdlv.authservice.service.RegistrationService;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

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

            registrationService.registerUser(request);

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

            registrationService.confirmEmail(request);

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
}
