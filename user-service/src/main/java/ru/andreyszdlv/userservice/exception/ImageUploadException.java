package ru.andreyszdlv.userservice.exception;

public class ImageUploadException extends RuntimeException{
    public ImageUploadException(String message) {
        super(message);
    }
}
