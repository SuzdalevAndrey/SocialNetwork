package ru.andreyszdlv.userservice.exception;

public class NoSuchFileException extends RuntimeException {
    public NoSuchFileException() {
    }

    public NoSuchFileException(String message) {
        super(message);
    }
}
