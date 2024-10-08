package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.authservice.model.EmailVerificationCode;
import ru.andreyszdlv.authservice.repository.EmailVerificationCodeRepo;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {
    private final EmailVerificationCodeRepo emailVerificationCodeRepository;

    public String generateAndSaveVerificationCode(String email){
        String verificationCode = GenerateCodeService.generateCode();

        EmailVerificationCode emailVerificationCode = new EmailVerificationCode();

        emailVerificationCode.setEmail(email);
        emailVerificationCode.setVerificationCode(verificationCode);
        emailVerificationCode.setExpirationTime(LocalDateTime.now());

        log.info("Saving the user with email: {} and his code", email);
        emailVerificationCodeRepository.save(emailVerificationCode);

        return verificationCode;
    }

    public boolean isValidCode(String email, String code){
        return emailVerificationCodeRepository.findByEmail(email)
                .orElseThrow(
                        ()->new NoSuchElementException("errors.404.email_not_found")
                )
                .getVerificationCode()
                .equals(code);
    }

}
