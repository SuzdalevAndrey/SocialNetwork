package ru.andreyszdlv.userservice.controller.advice;


import lombok.AllArgsConstructor;
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

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(final BindException ex, Locale locale) {
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
        return ResponseEntity.of(problemDetail).build();
    }

}
