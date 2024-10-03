package ru.andreyszdlv.authservice.exception;

public class ValidateTokenException extends RuntimeException{
    public ValidateTokenException() {
    }

    public ValidateTokenException(String message) {
        super(message);
    }

    public ValidateTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidateTokenException(Throwable cause) {
        super(cause);
    }

    public ValidateTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
