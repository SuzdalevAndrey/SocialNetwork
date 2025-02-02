package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.dto.kafka.RegisterUserKafkaDTO;
import ru.andreyszdlv.authservice.exception.RegisterUserNotFoundException;
import ru.andreyszdlv.authservice.exception.VerificationCodeHasExpiredException;
import ru.andreyszdlv.authservice.model.EmailVerificationCode;
import ru.andreyszdlv.authservice.repository.EmailVerificationCodeRepo;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeService {

    private final EmailVerificationCodeRepo emailVerificationCodeRepository;

    private final PendingUserService pendingUserService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public String generateAndSaveVerificationCode(String email){
        log.info("Executing generateAndSaveVerificationCode for email: {}", email);

        log.info("Generate code for email: {}", email);
        String verificationCode = GenerateCodeService.generateCode();

        log.info("Checking exist verification code for email: {}", email);
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

        log.info("Save verification code for email: {}", email);
        emailVerificationCodeRepository.save(emailVerificationCode);

        return verificationCode;
    }

    @Transactional(readOnly = true)
    public boolean isValidCode(String email, String code){
        log.info("Checking valid verification code for email: {}", email);
        return emailVerificationCodeRepository
                .findByEmail(email)
                .orElseThrow(
                        ()->new VerificationCodeHasExpiredException("errors.409.verification_code_has_expired")
                )
                .getVerificationCode()
                .equals(code);
    }

    @Transactional
    public void updateVerificationCode(String userEmail) {
        log.info("Executing updateVerificationCode for email: {}", userEmail);

        log.info("Checking register user with email: {}", userEmail);
        if(!pendingUserService.existsUserByEmail(userEmail)) {
            log.error("The user is already registered with email: {}", userEmail);
            throw new RegisterUserNotFoundException("errors.404.email_not_found");
        }

        log.info("Verification code generation for email: {}", userEmail);
        String verificationCode = this.generateAndSaveVerificationCode(userEmail);

        log.info("Sending message to kafka contains userEmail: {}", userEmail);
        applicationEventPublisher.publishEvent(new RegisterUserKafkaDTO(userEmail, verificationCode));
    }
}
