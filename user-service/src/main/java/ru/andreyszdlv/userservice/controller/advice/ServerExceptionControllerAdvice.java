package ru.andreyszdlv.userservice.controller.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.userservice.exception.DeleteImageException;
import ru.andreyszdlv.userservice.service.ProblemDetailService;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ServerExceptionControllerAdvice {

    private final ProblemDetailService problemDetailService;

    @ExceptionHandler({
            DeleteImageException.class
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