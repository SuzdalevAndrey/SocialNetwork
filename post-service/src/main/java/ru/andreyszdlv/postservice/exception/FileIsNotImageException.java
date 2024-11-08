package ru.andreyszdlv.postservice.exception;

public class FileIsNotImageException extends RuntimeException {
    public FileIsNotImageException(String message) {
        super(message);
    }
}
