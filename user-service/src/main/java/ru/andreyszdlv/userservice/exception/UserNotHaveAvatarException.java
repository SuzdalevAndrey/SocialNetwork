package ru.andreyszdlv.userservice.exception;

public class UserNotHaveAvatarException extends RuntimeException{
    public UserNotHaveAvatarException(String message) {
        super(message);
    }
}
