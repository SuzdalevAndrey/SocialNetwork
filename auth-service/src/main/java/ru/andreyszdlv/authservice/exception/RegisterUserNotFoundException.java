package ru.andreyszdlv.authservice.exception;

public class RegisterUserNotFoundException extends RuntimeException {
    public RegisterUserNotFoundException() {
    }

    public RegisterUserNotFoundException(String message) {
        super(message);
    }

    public RegisterUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegisterUserNotFoundException(Throwable cause) {
        super(cause);
    }

    public RegisterUserNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
