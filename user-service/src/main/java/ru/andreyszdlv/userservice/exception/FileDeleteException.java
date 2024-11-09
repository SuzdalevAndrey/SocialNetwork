package ru.andreyszdlv.userservice.exception;

public class FileDeleteException extends Exception{
    public FileDeleteException() {
    }

    public FileDeleteException(String message) {
        super(message);
    }
}
