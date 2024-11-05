package ru.andreyszdlv.postservice.exception;

public class CreateBucketException extends RuntimeException {

    public CreateBucketException() {
    }

    public CreateBucketException(String message) {
        super(message);
    }
}
