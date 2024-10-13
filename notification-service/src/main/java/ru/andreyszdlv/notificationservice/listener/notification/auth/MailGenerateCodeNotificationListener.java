package ru.andreyszdlv.notificationservice.listener.notification.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class MailGenerateCodeNotificationListener {
    private final KafkaProducerService kafkaProducerService;

    private final LocalizationService localizationService;

    private final MailSenderService mailSender;

    @EventListener
    public void handle(RegisterUserDTO user){
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
            mailSender.send(
                    user.email(),
                    header,
                    String.format(body, user.code())
            );
        }
        catch (MailException ex){
            kafkaProducerService.sendRegisterCompensation(user.email());
        }
    }
}
