package ru.andreyszdlv.userservice.exception;

public class UsersNoFriendsException extends RuntimeException{
    public UsersNoFriendsException() {
    }

    public UsersNoFriendsException(String message) {
        super(message);
    }

    public UsersNoFriendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsersNoFriendsException(Throwable cause) {
        super(cause);
    }

    public UsersNoFriendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
