package ru.andreyszdlv.postservice.exception;

public class PostNoSuchImageException extends RuntimeException{
    public PostNoSuchImageException(String message) {
        super(message);
    }
}
