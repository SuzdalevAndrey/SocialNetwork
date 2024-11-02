package ru.andreyszdlv.imageservice.exception;

public class EmptyImageFileException extends RuntimeException{
    public EmptyImageFileException(String message) {
        super(message);
    }
}
