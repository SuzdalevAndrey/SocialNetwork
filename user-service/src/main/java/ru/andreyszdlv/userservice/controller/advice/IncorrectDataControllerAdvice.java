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
import ru.andreyszdlv.userservice.exception.DifferentPasswordsException;
import ru.andreyszdlv.userservice.exception.NoSuchUserException;
import ru.andreyszdlv.userservice.service.LocalizationService;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class IncorrectDataControllerAdvice {

//    private final MessageSource messageSource;

    private final LocalizationService localizationService;

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundException(
            final NoSuchUserException ex,
            Locale locale) {

        log.error("Executing handleNotFoundException");

        ProblemDetail problemDetail = createProbemDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                locale);

        log.error("NotFoundException: ", ex);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(DifferentPasswordsException.class)
    public ResponseEntity<ProblemDetail> handleConflictException(
            final DifferentPasswordsException ex,
            Locale locale) {

        log.error("Executing handleConflictException");

        ProblemDetail problemDetail = createProbemDetail(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                locale);

        log.error("ConflictException " + ex);

        return ResponseEntity.of(problemDetail).build();
    }

    private ProblemDetail createProbemDetail(HttpStatus status, String message, Locale locale){
        return ProblemDetail.forStatusAndDetail(
                status,
                Optional.ofNullable(
                        localizationService.getLocalizedMessage(
                                message,
                                locale
                        )
                ).orElse("errors")
        );
    }

}
