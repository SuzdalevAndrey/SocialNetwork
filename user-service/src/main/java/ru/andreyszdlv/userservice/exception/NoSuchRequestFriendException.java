package ru.andreyszdlv.userservice.exception;

public class NoSuchRequestFriendException extends RuntimeException{
    public NoSuchRequestFriendException() {
    }

    public NoSuchRequestFriendException(String message) {
        super(message);
    }

    public NoSuchRequestFriendException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchRequestFriendException(Throwable cause) {
        super(cause);
    }

    public NoSuchRequestFriendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
