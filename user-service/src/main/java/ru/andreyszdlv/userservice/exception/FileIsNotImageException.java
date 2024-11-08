package ru.andreyszdlv.userservice.exception;

public class FileIsNotImageException extends RuntimeException {
    public FileIsNotImageException(String message) {
        super(message);
    }
}
