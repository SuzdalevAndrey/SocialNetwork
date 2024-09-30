package ru.andreyszdlv.authservice.controller.advice;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

@ControllerAdvice
@AllArgsConstructor
public class IncorrectDataControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundUser(final NoSuchElementException ex, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                Optional.ofNullable(messageSource.getMessage(ex.getMessage(),
                        null,
                        ex.getMessage(),
                        locale))
                        .orElse("errors"));
        return ResponseEntity.of(problemDetail).build();
    }
}
