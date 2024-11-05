package ru.andreyszdlv.postservice.controller.advice;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.postservice.exception.AlreadyLikedException;
import ru.andreyszdlv.postservice.exception.AnotherUserCreatePostException;
import ru.andreyszdlv.postservice.exception.AnotherUsersCommentException;
import ru.andreyszdlv.postservice.exception.ImagePostCountException;
import ru.andreyszdlv.postservice.exception.NoLikedPostThisUserException;
import ru.andreyszdlv.postservice.exception.NoSuchCommentException;
import ru.andreyszdlv.postservice.exception.NoSuchImageException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.exception.PostNoSuchImageException;
import ru.andreyszdlv.postservice.service.ProblemDetailService;

import java.util.Locale;


@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class IncorrectDataControllerAdvice {

    private final ProblemDetailService problemDetailService;

    @ExceptionHandler(FeignException.FeignClientException.class)
    public ResponseEntity<ProblemDetail> handleFeignException(FeignException ex,
                                                              Locale locale){
        log.error("Executing handleFeignException");

        ProblemDetail problemDetail = problemDetailService.createProblemDetail(
                HttpStatus.valueOf(ex.status()),
                ex.getMessage(),
                locale
        );

        log.error("FeignException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler({
            NoSuchPostException.class,
            NoLikedPostThisUserException.class,
            NoSuchCommentException.class,
            NoSuchImageException.class,
            PostNoSuchImageException.class
    })
    public ResponseEntity<ProblemDetail> handleNotFoundException(
            RuntimeException ex,
            Locale locale){
        log.error("Executing handleNotFoundException");

        ProblemDetail problemDetail = problemDetailService.createProblemDetail(
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
            AnotherUserCreatePostException.class,
            ImagePostCountException.class
    })
    public ResponseEntity<ProblemDetail> handleConflictException(
            RuntimeException ex,
            Locale locale){
        log.error("Executing handleConflictException");

        ProblemDetail problemDetail = problemDetailService.createProblemDetail(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                locale
        );

        log.error("ConflictException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }
}
