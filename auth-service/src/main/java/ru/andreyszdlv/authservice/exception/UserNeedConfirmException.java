package ru.andreyszdlv.authservice.exception;

public class UserNeedConfirmException extends RuntimeException{
    public UserNeedConfirmException() {

    }

    public UserNeedConfirmException(String message) {
        super(message);
    }

    public UserNeedConfirmException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNeedConfirmException(Throwable cause) {
        super(cause);
    }

    public UserNeedConfirmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
