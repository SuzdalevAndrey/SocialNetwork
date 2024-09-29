package ru.andreyszdlv.postservice.controller.advice;

import lombok.AllArgsConstructor;
import org.apache.catalina.valves.rewrite.RewriteCond;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.postservice.execption.NoSuchPostException;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

@ControllerAdvice
@AllArgsConstructor
public class InvalidDataControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException ex, Locale locale){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                Optional.ofNullable(
                    messageSource.getMessage(
                            ex.getMessage(),
                            null,
                            ex.getMessage(),
                            locale
                    )
                ).orElse("")
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
}
