package ru.andreyszdlv.userservice.exception;

public class UsersAlreadyFriendsException extends RuntimeException{
    public UsersAlreadyFriendsException() {
    }

    public UsersAlreadyFriendsException(String message) {
        super(message);
    }

    public UsersAlreadyFriendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsersAlreadyFriendsException(Throwable cause) {
        super(cause);
    }

    public UsersAlreadyFriendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
