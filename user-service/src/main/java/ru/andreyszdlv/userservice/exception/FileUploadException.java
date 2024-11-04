package ru.andreyszdlv.userservice.exception;

public class FileUploadException extends RuntimeException{
    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException() {
    }
}
