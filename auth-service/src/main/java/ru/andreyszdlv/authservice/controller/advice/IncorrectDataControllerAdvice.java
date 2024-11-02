package ru.andreyszdlv.authservice.controller.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.andreyszdlv.authservice.service.ProblemDetailService;

import java.util.Locale;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class IncorrectDataControllerAdvice {

    private final ProblemDetailService problemDetailService;

    @ExceptionHandler({
            UsernameNotFoundException.class,
            RegisterUserNotFoundException.class
    })
    public ResponseEntity<ProblemDetail> handleNotFoundException(
            RuntimeException ex,
            Locale locale) {

        log.error("Executing handleNotFoundException in IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = problemDetailService.createProblemDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                locale
        );

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
            RuntimeException ex,
            Locale locale
    ) {
        log.error("Executing handleConflictException in IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = problemDetailService.createProblemDetail(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                locale
        );

        log.error("ConflictException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }
}
