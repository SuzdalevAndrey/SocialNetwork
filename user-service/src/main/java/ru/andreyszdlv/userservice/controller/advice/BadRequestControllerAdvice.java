package ru.andreyszdlv.userservice.controller.advice;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.validation.BindException;
import ru.andreyszdlv.userservice.exception.ImageUploadException;
import ru.andreyszdlv.userservice.service.LocalizationService;

import java.util.Locale;
import java.util.Optional;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class BadRequestControllerAdvice {

    private final LocalizationService localizationService;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(final BindException ex, Locale locale) {

        log.error("Executing handleBindException");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                Optional.ofNullable(
                        localizationService.getLocalizedMessage(
                            "errors.400.request.title",
                            locale
                        )
                ).orElse("errors")
        );

        problemDetail.setProperty(
                "errors",
                ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList()
        );

        log.error("BindException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ProblemDetail> handleImageUploadException(ImageUploadException ex, Locale locale) {

        log.error("Executing handleImageUploadException");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                Optional.ofNullable(
                        localizationService.getLocalizedMessage(
                                ex.getMessage(),
                                locale
                        )
                ).orElse("errors")
        );

        log.error("ImageUploadException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

}
