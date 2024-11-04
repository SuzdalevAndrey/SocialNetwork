package ru.andreyszdlv.userservice.exception;

public class FileDeleteException extends RuntimeException{
    public FileDeleteException() {
    }

    public FileDeleteException(String message) {
        super(message);
    }
}
