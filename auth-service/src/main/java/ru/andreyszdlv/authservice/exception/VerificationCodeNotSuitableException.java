package ru.andreyszdlv.authservice.exception;

public class VerificationCodeNotSuitableException extends RuntimeException{
    public VerificationCodeNotSuitableException() {
    }

    public VerificationCodeNotSuitableException(String message) {
        super(message);
    }

    public VerificationCodeNotSuitableException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerificationCodeNotSuitableException(Throwable cause) {
        super(cause);
    }

    public VerificationCodeNotSuitableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
