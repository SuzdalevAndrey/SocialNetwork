package ru.andreyszdlv.postservice.controller.advice;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.postservice.service.ProblemDetailService;

import java.util.Locale;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class BadRequestControllerAdvice {

    private final ProblemDetailService problemDetailService;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(BindException ex, Locale locale) {
        log.error("Executing handleBindException");

        ProblemDetail problemDetail = problemDetailService.createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "error.400.request.title",
                locale
        );

        problemDetail.setProperty(
                "errors",
                ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList());

        log.error("Bad request: {}", problemDetail);

        return ResponseEntity.of(problemDetail).build();
    }

}
