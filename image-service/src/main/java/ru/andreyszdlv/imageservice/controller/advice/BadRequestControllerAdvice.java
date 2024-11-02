package ru.andreyszdlv.imageservice.controller.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.imageservice.exception.EmptyImageFileException;
import ru.andreyszdlv.imageservice.exception.ImageUploadException;
import ru.andreyszdlv.imageservice.service.ProblemDetailService;

import java.util.Locale;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class BadRequestControllerAdvice {

    private final ProblemDetailService problemDetailService;

    @ExceptionHandler({
            EmptyImageFileException.class,
            ImageUploadException.class,
    })
    public ResponseEntity<ProblemDetail> handleBadRequest(ImageUploadException ex, Locale locale) {

        log.error("Executing handleBadRequest");

        ProblemDetail problemDetail = problemDetailService.createProblemDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                locale
        );

        log.error("BadRequest: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }
}
