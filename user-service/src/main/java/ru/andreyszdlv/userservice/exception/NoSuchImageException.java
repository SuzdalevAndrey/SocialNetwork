package ru.andreyszdlv.userservice.exception;

public class NoSuchImageException extends RuntimeException{
    public NoSuchImageException(String message) {
        super(message);
    }
}
