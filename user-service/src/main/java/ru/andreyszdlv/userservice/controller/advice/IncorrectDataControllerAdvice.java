package ru.andreyszdlv.userservice.controller.advice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andreyszdlv.springbootstarters3loadimage.exception.NoSuchImageException;
import ru.andreyszdlv.userservice.exception.DifferentPasswordsException;
import ru.andreyszdlv.userservice.exception.NoSuchRequestFriendException;
import ru.andreyszdlv.userservice.exception.NoSuchUserException;
import ru.andreyszdlv.userservice.exception.RequestInFriendsAlreadySendException;
import ru.andreyszdlv.userservice.exception.UserAlreadyHaveAvatarException;
import ru.andreyszdlv.userservice.exception.UserNotHaveAvatarException;
import ru.andreyszdlv.userservice.exception.UsersAlreadyFriendsException;
import ru.andreyszdlv.userservice.exception.UsersNoFriendsException;
import ru.andreyszdlv.userservice.service.ProblemDetailService;

import java.util.Locale;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class IncorrectDataControllerAdvice {

    private final ProblemDetailService problemDetailService;

    @ExceptionHandler({
            NoSuchUserException.class,
            NoSuchRequestFriendException.class,
            NoSuchImageException.class
    })
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

    @ExceptionHandler({
            DifferentPasswordsException.class,
            RequestInFriendsAlreadySendException.class,
            UsersAlreadyFriendsException.class,
            UsersNoFriendsException.class,
            UserAlreadyHaveAvatarException.class,
            UserNotHaveAvatarException.class
    })
    public ResponseEntity<ProblemDetail> handleConflictException(
            RuntimeException ex,
            Locale locale) {

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
