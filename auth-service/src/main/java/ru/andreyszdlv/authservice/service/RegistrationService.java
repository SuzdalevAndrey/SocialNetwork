package ru.andreyszdlv.authservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.client.UserServiceClient;
import ru.andreyszdlv.authservice.dto.controller.ConfirmEmailRequestDTO;
import ru.andreyszdlv.authservice.dto.controller.RegisterRequestDTO;
import ru.andreyszdlv.authservice.exception.UserAlreadyRegisteredException;
import ru.andreyszdlv.authservice.exception.UserNeedConfirmException;
import ru.andreyszdlv.authservice.exception.VerificationCodeNotSuitableException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationService {

    private final VerificationCodeService verificationCodeService;

    private final PendingUserService pendingUserService;

    private final UserServiceClient userServiceClient;

    private final KafkaProducerService kafkaProducerService;

    private final MeterRegistry meterRegistry;

    @Transactional
    public void registerUser(RegisterRequestDTO request) {
        log.info("Executing registerUser for email: {} and name: {}",
                request.email(),
                request.name());

        log.info("Checking registered user with email: {}", request.email());
        if(userServiceClient.existsUserByEmail(request.email()).getBody()) {
            log.error("User already registered with email: {}", request.email());
            throw new UserAlreadyRegisteredException("errors.409.user_already_register");
        }

        log.info("Checking user need confirm email: {}", request.email());
        if(pendingUserService.existsUserByEmail(request.email())){
            log.error("User need confirm email: {}", request.email());
            throw new UserNeedConfirmException("errors.409.need_confirm_email");
        }

        log.info("Verification code generation");
        String verificationCode = verificationCodeService
                .generateAndSaveVerificationCode(request.email());

        log.info("Save user to temporary database");
        pendingUserService.savePendingUser(request.name(), request.email(), request.password());

        log.info("Send message to kafka contains userEmail: {} and code", request.email());
        kafkaProducerService.sendRegisterEvent(request.email(), verificationCode);

        meterRegistry.counter("user_registry").increment();
    }

    @Transactional
    public void confirmEmail(ConfirmEmailRequestDTO request){
        log.info("Executing confirmEmail in AuthService for email: {}", request.email());

        log.info("Comparison verification code with code sent by user");
        if(verificationCodeService.isValidCode(request.email(), request.code())){
            log.info("The code from user is valid");

            log.info("Saving the user to a permanent database");
            pendingUserService.savePendingUserInPermanentBD(request.email());

            return;
        }
        log.error("Code sent user with email: {} not valid", request.email());
        throw new VerificationCodeNotSuitableException("errors.409.verification_token_is_not_valid");
    }
}
