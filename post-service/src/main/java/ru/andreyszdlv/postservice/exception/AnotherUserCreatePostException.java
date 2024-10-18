package ru.andreyszdlv.postservice.exception;

public class AnotherUserCreatePostException extends RuntimeException {
    public AnotherUserCreatePostException() {

    }

    public AnotherUserCreatePostException(String message) {
        super(message);
    }

    public AnotherUserCreatePostException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnotherUserCreatePostException(Throwable cause) {
        super(cause);
    }

    public AnotherUserCreatePostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
