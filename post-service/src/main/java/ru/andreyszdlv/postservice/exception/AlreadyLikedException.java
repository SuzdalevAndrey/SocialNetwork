package ru.andreyszdlv.postservice.exception;

public class AlreadyLikedException extends RuntimeException{
    public AlreadyLikedException() {
    }

    public AlreadyLikedException(String message) {
        super(message);
    }

    public AlreadyLikedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyLikedException(Throwable cause) {
        super(cause);
    }

    public AlreadyLikedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
