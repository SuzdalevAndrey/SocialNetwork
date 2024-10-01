package ru.andreyszdlv.postservice.controller.advice;


import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;
import java.util.Optional;

@ControllerAdvice
@AllArgsConstructor
public class BadRequestControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(BindException ex, Locale locale){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                Optional.ofNullable(messageSource.getMessage(
                        "error.400.request.title",
                        null,
                        "error.request.400.title",
                        locale
                )).orElse("errors")
        );
        problemDetail.setProperty(
                "errors",
                ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList());
        return ResponseEntity.of(problemDetail).build();
    }

}
