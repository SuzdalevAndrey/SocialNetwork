package ru.andreyszdlv.postservice.exception;

public class EmptyImageException extends RuntimeException {
    public EmptyImageException(String message) {
        super(message);
    }
}
