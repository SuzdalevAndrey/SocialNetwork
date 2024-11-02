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
import ru.andreyszdlv.imageservice.exception.NoSuchImageException;
import ru.andreyszdlv.imageservice.service.ProblemDetailService;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class IncorrectDataControllerAdvice {

    private final ProblemDetailService problemDetailService;

    @ExceptionHandler(NoSuchImageException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundException(
            RuntimeException ex,
            Locale locale) {

        log.error("Executing handleNotFoundException");

        ProblemDetail problemDetail = problemDetailService.createProblemDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                locale
        );

        log.error("NotFoundException: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }
}
