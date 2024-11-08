package ru.andreyszdlv.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.authservice.dto.controller.UpdateVerifiCodeRequestDTO;
import ru.andreyszdlv.authservice.service.LocalizationService;
import ru.andreyszdlv.authservice.service.VerificationCodeService;

import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeController {

    private final VerificationCodeService verificationCodeService;

    private final LocalizationService localizationService;

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

            verificationCodeService.updateVerificationCode(request.email());

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
}
