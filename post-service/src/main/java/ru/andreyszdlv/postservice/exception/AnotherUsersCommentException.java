package ru.andreyszdlv.postservice.exception;

public class AnotherUsersCommentException extends RuntimeException{
    public AnotherUsersCommentException() {
    }

    public AnotherUsersCommentException(String message) {
        super(message);
    }

    public AnotherUsersCommentException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnotherUsersCommentException(Throwable cause) {
        super(cause);
    }

    public AnotherUsersCommentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
