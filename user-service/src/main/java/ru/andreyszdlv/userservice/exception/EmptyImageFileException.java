package ru.andreyszdlv.userservice.exception;

public class EmptyImageFileException extends RuntimeException{
    public EmptyImageFileException(String message) {
        super(message);
    }
}
