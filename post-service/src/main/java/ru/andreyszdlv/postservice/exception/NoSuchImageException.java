package ru.andreyszdlv.postservice.exception;

public class NoSuchImageException extends RuntimeException {
    public NoSuchImageException(String message) {
        super(message);
    }
}
