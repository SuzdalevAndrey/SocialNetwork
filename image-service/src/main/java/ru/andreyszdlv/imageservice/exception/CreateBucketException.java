package ru.andreyszdlv.imageservice.exception;

public class CreateBucketException extends RuntimeException{
    public CreateBucketException(String message) {
        super(message);
    }
}
