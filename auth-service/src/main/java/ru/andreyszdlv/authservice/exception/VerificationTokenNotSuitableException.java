package ru.andreyszdlv.authservice.exception;

public class VerificationTokenNotSuitableException extends RuntimeException{
    public VerificationTokenNotSuitableException() {
    }

    public VerificationTokenNotSuitableException(String message) {
        super(message);
    }

    public VerificationTokenNotSuitableException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerificationTokenNotSuitableException(Throwable cause) {
        super(cause);
    }

    public VerificationTokenNotSuitableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
