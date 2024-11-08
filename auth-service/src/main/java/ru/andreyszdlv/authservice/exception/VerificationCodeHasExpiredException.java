package ru.andreyszdlv.authservice.exception;

public class VerificationCodeHasExpiredException extends RuntimeException{
    public VerificationCodeHasExpiredException(String message) {
        super(message);
    }
}
