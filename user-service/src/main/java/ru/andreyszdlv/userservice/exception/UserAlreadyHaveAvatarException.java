package ru.andreyszdlv.userservice.exception;

public class UserAlreadyHaveAvatarException extends RuntimeException{
    public UserAlreadyHaveAvatarException(String message) {
        super(message);
    }
}
