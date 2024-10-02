package ru.andreyszdlv.userservice.controller.advice;


import lombok.AllArgsConstructor;
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

import java.util.Locale;
import java.util.Optional;

@ControllerAdvice
@AllArgsConstructor
public class BadRequestControllerAdvice {

    private final MessageSource messageSource;

    private final static Logger log = LoggerFactory.getLogger(BadRequestControllerAdvice.class);

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(final BindException ex, Locale locale) {

        log.error("The method handleBindException in the BadRequestControllerAdvice");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                Optional.ofNullable(
                        messageSource.getMessage(
                            "errors.400.request.title",
                            null,
                            "errors.400.request.title",
                            locale))
                        .orElse("errors"));

        problemDetail.setProperty(
                "errors",
            ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList()
        );

        log.error("Bad request: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

}
