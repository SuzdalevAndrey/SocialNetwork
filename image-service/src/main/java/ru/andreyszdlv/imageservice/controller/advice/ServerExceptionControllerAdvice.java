package ru.andreyszdlv.imageservice.controller.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.imageservice.exception.CreateBucketException;
import ru.andreyszdlv.imageservice.exception.ImageUploadException;
import ru.andreyszdlv.imageservice.service.ProblemDetailService;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ServerExceptionControllerAdvice {

    private final ProblemDetailService problemDetailService;

    @ExceptionHandler({
            CreateBucketException.class,
            ImageUploadException.class
    })
    public ResponseEntity<ProblemDetail> handleInternalServerException(RuntimeException ex,
                                                                       Locale locale) {
        log.error("Executing handleInternalServerException");
        ProblemDetail problemDetail = problemDetailService.createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                locale
        );

        log.error("InternalServerException: {}", problemDetail);
        return ResponseEntity.of(problemDetail).build();
    }
}