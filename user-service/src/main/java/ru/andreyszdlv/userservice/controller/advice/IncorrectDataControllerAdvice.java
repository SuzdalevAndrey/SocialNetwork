package ru.andreyszdlv.userservice.controller.advice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class IncorrectDataControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundUserException(
            final NoSuchElementException ex,
            Locale locale) {

        log.error("The method handleNotFoundUserException in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = createProbemDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                locale);

        log.error("NoSuchElementException: ", ex);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleDifferentPasswordsException(
            final BadCredentialsException ex,
            Locale locale) {

        log.error("The method handleDifferentPasswordsException in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = createProbemDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                locale);

        log.error("Password different: " + ex);

        return ResponseEntity.of(problemDetail).build();
    }

    private ProblemDetail createProbemDetail(HttpStatus status, String message, Locale locale){
        return ProblemDetail.forStatusAndDetail(
                status,
                Optional.ofNullable(
                        messageSource.getMessage(
                                message,
                                null,
                                message,
                                locale
                        )).orElse("errors")
        );
    }

}
