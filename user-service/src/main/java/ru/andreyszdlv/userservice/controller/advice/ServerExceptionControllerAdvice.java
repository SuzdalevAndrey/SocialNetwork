package ru.andreyszdlv.userservice.controller.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.userservice.exception.CreateBucketException;
import ru.andreyszdlv.userservice.exception.ImageUploadException;
import ru.andreyszdlv.userservice.service.LocalizationService;

import java.util.Locale;
import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ServerExceptionControllerAdvice {

    private final LocalizationService localizationService;

    @ExceptionHandler({
            CreateBucketException.class,
            ImageUploadException.class
    })
    public ResponseEntity<ProblemDetail> handleInternalServerException(RuntimeException ex,
                                                                       Locale locale) {
        log.error("Executing handleInternalServerException");
        ProblemDetail problemDetail = createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                locale);

        log.error("InternalServerException: {}", problemDetail);
        return ResponseEntity.of(problemDetail).build();
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String message, Locale locale) {
        return ProblemDetail.forStatusAndDetail(
                status,
                Optional.ofNullable(
                    localizationService.getLocalizedMessage(
                            message,
                            locale
                    )
                ).orElse("errors")
        );
    }
}
