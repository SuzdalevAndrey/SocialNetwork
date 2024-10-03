package ru.andreyszdlv.authservice.controller.advice;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.authservice.exception.ValidateTokenException;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

@ControllerAdvice
@AllArgsConstructor
public class IncorrectDataControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(IncorrectDataControllerAdvice.class);

    private final MessageSource messageSource;

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundUserException(
            final UsernameNotFoundException ex,
            Locale locale) {

        log.error("Executing handleNotFoundUserException method in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = generateProblemDetail(HttpStatus.NOT_FOUND,
                ex.getMessage(),
                locale);

        log.error("UsernameNotFoundException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(ValidateTokenException.class)
    public ResponseEntity<ProblemDetail> handleValidateTokenException(
            final ValidateTokenException ex,
            Locale locale
    ) {
        log.error("Executing handleValidateTokenException method in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = generateProblemDetail(HttpStatus.CONFLICT,
                ex.getMessage(),
                locale);

        log.error("Token is not valid: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

    private ProblemDetail generateProblemDetail(HttpStatus status,
                                                String message,
                                                Locale locale){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                messageSource.getMessage(
                        message,
                        null,
                        message,
                        locale
                )
        );

        return problemDetail;
    }
}
