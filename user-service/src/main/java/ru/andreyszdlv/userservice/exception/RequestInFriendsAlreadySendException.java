package ru.andreyszdlv.userservice.exception;

public class RequestInFriendsAlreadySendException extends RuntimeException{
    public RequestInFriendsAlreadySendException() {
    }

    public RequestInFriendsAlreadySendException(String message) {
        super(message);
    }

    public RequestInFriendsAlreadySendException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestInFriendsAlreadySendException(Throwable cause) {
        super(cause);
    }

    public RequestInFriendsAlreadySendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
