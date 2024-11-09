package ru.andreyszdlv.userservice.exception;

public class NoSuchFileException extends Exception {
    public NoSuchFileException() {
    }

    public NoSuchFileException(String message) {
        super(message);
    }
}
