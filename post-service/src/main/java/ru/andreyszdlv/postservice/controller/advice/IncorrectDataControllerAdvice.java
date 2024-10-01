package ru.andreyszdlv.postservice.controller.advice;

import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.postservice.exception.AlreadyLikedException;
import ru.andreyszdlv.postservice.exception.NoLikedPostThisUserException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;

import java.util.Locale;
import java.util.Optional;


@ControllerAdvice
@AllArgsConstructor
public class IncorrectDataControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(FeignException.FeignClientException.class)
    public ResponseEntity<ProblemDetail> handleFeignException(FeignException ex, Locale locale){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(ex.status()),
                Optional.ofNullable(
                        messageSource.getMessage(
                        ex.getMessage(),
                        null,
                        ex.getMessage(),
                        locale
                )).orElse("errors")
        );
        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(NoSuchPostException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchPostException(NoSuchPostException ex, Locale locale){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                Optional.ofNullable(
                messageSource.getMessage(
                        ex.getMessage(),
                        null,
                        ex.getMessage(),
                        locale
                )).orElse("errors")
        );
        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(AlreadyLikedException.class)
    public ResponseEntity<ProblemDetail> handleAlreadyLikedException(AlreadyLikedException ex, Locale locale){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                Optional.ofNullable(
                messageSource.getMessage(
                        ex.getMessage(),
                        null,
                        ex.getMessage(),
                        locale
                )).orElse("errors")
        );
        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(NoLikedPostThisUserException.class)
    public ResponseEntity<ProblemDetail> handleNoLikedPostThisUserException(NoLikedPostThisUserException ex, Locale locale){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                Optional.ofNullable(
                        messageSource.getMessage(
                                ex.getMessage(),
                                null,
                                ex.getMessage(),
                                locale
                        )).orElse("errors")
        );
        return ResponseEntity.of(problemDetail).build();
    }
}
