package ru.andreyszdlv.userservice.exception;

public class EmptyImageException extends RuntimeException{
    public EmptyImageException(String message) {
        super(message);
    }
}
