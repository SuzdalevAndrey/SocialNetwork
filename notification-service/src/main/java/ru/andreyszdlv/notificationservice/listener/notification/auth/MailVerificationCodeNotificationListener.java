package ru.andreyszdlv.notificationservice.listener.notification.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.auth.RegisterUserDTO;
import ru.andreyszdlv.notificationservice.service.KafkaProducerService;
import ru.andreyszdlv.notificationservice.service.LocalizationService;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailVerificationCodeNotificationListener {

    private final KafkaProducerService kafkaProducerService;

    private final LocalizationService localizationService;

    private final MailSenderService mailSender;

    @EventListener
    public void handle(RegisterUserDTO user){
        log.info("Executing handle register user event for email: {}", user.email());

        Locale locale = Locale.getDefault();

        String header = localizationService.getLocalizedMessage(
                "message.register.header",
                locale
        );

        String body = localizationService.getLocalizedMessage(
                "message.register.body",
                locale
        );

        try {
            log.info("Sending email with verification code to user: {}", user.email());
            mailSender.send(
                    user.email(),
                    header,
                    String.format(body, user.code())
            );
        }
        catch (MailException ex){
            log.error("Send mail fail, sendFailureRegisterMailEvent in kafka for email: {}",
                    user.email());
            kafkaProducerService.sendFailureRegisterMailEvent(user.email());
        }
    }
}
