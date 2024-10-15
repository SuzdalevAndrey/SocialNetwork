package ru.andreyszdlv.authservice.controller.advice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.authservice.exception.UserNeedConfirmException;
import ru.andreyszdlv.authservice.exception.RegisterUserNotFoundException;
import ru.andreyszdlv.authservice.exception.UserAlreadyRegisteredException;
import ru.andreyszdlv.authservice.exception.ValidateTokenException;
import ru.andreyszdlv.authservice.exception.VerificationCodeNotSuitableException;

import java.util.Locale;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class IncorrectDataControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler({
            UsernameNotFoundException.class,
            RegisterUserNotFoundException.class
    })
    public ResponseEntity<ProblemDetail> handleNotFoundException(
            UsernameNotFoundException ex,
            Locale locale) {

        log.error("Executing handleNotFoundException method in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = generateProblemDetail(HttpStatus.NOT_FOUND,
                ex.getMessage(),
                locale);

        log.error("NotFoundException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler({
            ValidateTokenException.class,
            UserAlreadyRegisteredException.class,
            VerificationCodeNotSuitableException.class,
            UserNeedConfirmException.class
    })
    public ResponseEntity<ProblemDetail> handleConflictException(
            ValidateTokenException ex,
            Locale locale
    ) {
        log.error("Executing handleConflictException method in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = generateProblemDetail(HttpStatus.CONFLICT,
                ex.getMessage(),
                locale);

        log.error("ConflictException: {}", problemDetail);

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
