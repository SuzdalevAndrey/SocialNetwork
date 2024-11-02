package ru.andreyszdlv.imageservice.exception;

public class NoSuchImageException extends RuntimeException{
    public NoSuchImageException(String message) {
        super(message);
    }
}
