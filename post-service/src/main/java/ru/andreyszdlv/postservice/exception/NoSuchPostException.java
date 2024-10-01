package ru.andreyszdlv.postservice.exception;

public class NoSuchPostException extends RuntimeException{

    public NoSuchPostException() {
    }

    public NoSuchPostException(String message) {
        super(message);
    }

    public NoSuchPostException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchPostException(Throwable cause) {
        super(cause);
    }

    public NoSuchPostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
