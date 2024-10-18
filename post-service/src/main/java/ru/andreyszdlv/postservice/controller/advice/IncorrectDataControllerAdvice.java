package ru.andreyszdlv.postservice.controller.advice;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.postservice.exception.AlreadyLikedException;
import ru.andreyszdlv.postservice.exception.AnotherUserCreatePostException;
import ru.andreyszdlv.postservice.exception.AnotherUsersCommentException;
import ru.andreyszdlv.postservice.exception.NoLikedPostThisUserException;
import ru.andreyszdlv.postservice.exception.NoSuchCommentException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;

import java.util.Locale;
import java.util.Optional;


@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class IncorrectDataControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(FeignException.FeignClientException.class)
    public ResponseEntity<ProblemDetail> handleFeignException(FeignException ex,
                                                              Locale locale){
        log.error("Executing handleFeignException");

        ProblemDetail problemDetail = createProbemDetail(HttpStatus.valueOf(ex.status()),
                ex.getMessage(),
                locale);

        log.error("FeignException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler({
            NoSuchPostException.class,
            NoLikedPostThisUserException.class,
            NoSuchCommentException.class
    })
    public ResponseEntity<ProblemDetail> handleNotFoundException(
            RuntimeException ex,
            Locale locale){
        log.error("Executing handleNotFoundException");

        ProblemDetail problemDetail = createProbemDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                locale
        );

        log.error("NotFoundException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler({
            AlreadyLikedException.class,
            AnotherUsersCommentException.class,
            AnotherUserCreatePostException.class
    })
    public ResponseEntity<ProblemDetail> handleConflictException(
            RuntimeException ex,
            Locale locale){
        log.error("Executing handleConflictException");

        ProblemDetail problemDetail = createProbemDetail(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                locale
        );

        log.error("ConflictException: {}", problemDetail);

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
                        )
                ).orElse("errors")
        );
    }
}
