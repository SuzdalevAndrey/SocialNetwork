package ru.andreyszdlv.postservice.exception;

public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message) {
        super(message);
    }
}
