package ru.andreyszdlv.userservice.exception;

public class CreateBucketException extends RuntimeException{
    public CreateBucketException(String message) {
        super(message);
    }
}
