package ru.andreyszdlv.postservice.exception;

public class NoSuchCommentException extends RuntimeException{
    public NoSuchCommentException() {
    }

    public NoSuchCommentException(String message) {
        super(message);
    }

    public NoSuchCommentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchCommentException(Throwable cause) {
        super(cause);
    }

    public NoSuchCommentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
