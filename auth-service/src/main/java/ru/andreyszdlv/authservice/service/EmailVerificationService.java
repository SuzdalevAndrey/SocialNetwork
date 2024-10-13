package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.exception.RegisterUserNotFoundException;
import ru.andreyszdlv.authservice.model.EmailVerificationCode;
import ru.andreyszdlv.authservice.repository.EmailVerificationCodeRepo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationCodeRepo emailVerificationCodeRepository;

    @Transactional
    public String generateAndSaveVerificationCode(String email){
        String verificationCode = GenerateCodeService.generateCode();

        EmailVerificationCode emailVerificationCode = emailVerificationCodeRepository
                .findByEmail(email)
                .orElse(new EmailVerificationCode());

        emailVerificationCode.setEmail(email);
        emailVerificationCode.setVerificationCode(verificationCode);
        emailVerificationCode.setExpirationTime(
                LocalDateTime
                .now()
                .plusMinutes(15)
        );

        emailVerificationCodeRepository.save(emailVerificationCode);

        return verificationCode;
    }

    @Transactional(readOnly = true)
    public boolean isValidCode(String email, String code){
        return emailVerificationCodeRepository.findByEmail(email)
                .orElseThrow(
                        ()->new RegisterUserNotFoundException("errors.404.email_not_found")
                )
                .getVerificationCode()
                .equals(code);
    }

}
