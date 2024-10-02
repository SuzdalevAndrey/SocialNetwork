package ru.andreyszdlv.userservice.controller.advice;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger log = LoggerFactory.getLogger(IncorrectDataControllerAdvice.class);

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundUserException(final NoSuchElementException ex, Locale locale) {

        log.info("The method handleNotFoundUserException in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                Optional.ofNullable(messageSource.getMessage(ex.getMessage(),
                        null,
                        ex.getMessage(),
                        locale))
                        .orElse("errors"));

        log.error("NoSuchElementException: ", ex);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleDifferentPasswordsException(final BadCredentialsException ex, Locale locale) {

        log.info("The method handleDifferentPasswordsException in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                Optional.ofNullable(messageSource.getMessage(ex.getMessage(),
                                null,
                                ex.getMessage(),
                                locale))
                        .orElse("errors"));

        log.error("Password different: " + ex);

        return ResponseEntity.of(problemDetail).build();
    }

}
