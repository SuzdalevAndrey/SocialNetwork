package ru.andreyszdlv.postservice.exception;

public class NoLikedPostThisUserException extends RuntimeException{
    public NoLikedPostThisUserException() {
    }

    public NoLikedPostThisUserException(String message) {
        super(message);
    }

    public NoLikedPostThisUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoLikedPostThisUserException(Throwable cause) {
        super(cause);
    }

    public NoLikedPostThisUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
