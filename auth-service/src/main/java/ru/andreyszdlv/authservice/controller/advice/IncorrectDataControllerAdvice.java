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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundUserException(
            UsernameNotFoundException ex,
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
            ValidateTokenException ex,
            Locale locale
    ) {
        log.error("Executing handleValidateTokenException method in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = generateProblemDetail(HttpStatus.CONFLICT,
                ex.getMessage(),
                locale);

        log.error("Token is not valid: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(RegisterUserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleRegisterUserNotFoundException(
            RegisterUserNotFoundException ex,
            Locale locale){
        log.error("Executing handleRegisterUserNotFoundException method in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = generateProblemDetail(HttpStatus.NOT_FOUND,
                ex.getMessage(),
                locale);

        log.error("RegisterUserNotFound: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyRegisteredException(
            UserAlreadyRegisteredException ex,
            Locale locale){
        log.error("Executing handleUserAlreadyRegisteredException method in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = generateProblemDetail(HttpStatus.CONFLICT,
                ex.getMessage(),
                locale);

        log.error("UserAlreadyRegisteredException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(UserNeedConfirmException.class)
    public ResponseEntity<ProblemDetail> handleUserNeedConfirmException(
            UserNeedConfirmException ex,
            Locale locale
    ){
        log.error("Executing handleUserNeedConfirmException method in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = generateProblemDetail(HttpStatus.CONFLICT,
                ex.getMessage(),
                locale);

        log.error("UserNeedConfirmException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(VerificationCodeNotSuitableException.class)
    public ResponseEntity<ProblemDetail> handleVerificationCodeNotSuitableException(
            VerificationCodeNotSuitableException ex,
            Locale locale)
    {
        log.error("Executing handleVerificationCodeNotSuitableException method in the IncorrectDataControllerAdvice");

        ProblemDetail problemDetail = generateProblemDetail(HttpStatus.CONFLICT,
                ex.getMessage(),
                locale);

        log.error("VerificationCodeNotSuitableException: {}", problemDetail);

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
