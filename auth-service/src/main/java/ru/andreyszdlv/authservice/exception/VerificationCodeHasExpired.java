package ru.andreyszdlv.authservice.exception;

public class VerificationCodeHasExpired extends RuntimeException{
    public VerificationCodeHasExpired(String message) {
        super(message);
    }
}
