package ru.andreyszdlv.authservice.service;

import java.security.SecureRandom;
import java.util.Random;

public class GenerateCodeService {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int CODE_LENGTH = 6;

    private static final Random RANDOM = new SecureRandom();

    public static String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}
