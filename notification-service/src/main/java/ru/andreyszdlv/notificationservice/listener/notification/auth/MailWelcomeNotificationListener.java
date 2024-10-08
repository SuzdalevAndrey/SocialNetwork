package ru.andreyszdlv.notificationservice.listener.notification.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.auth.LoginUserDTO;
import ru.andreyszdlv.notificationservice.service.LocalizationService;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MailWelcomeNotificationListener {
    private final LocalizationService localizationService;

    private final MailSenderService mailSender;

    @EventListener
    public void handle(LoginUserDTO user){

        Locale locale = Locale.getDefault();

        String header = localizationService.getLocalizedMessage(
                "message.welcome.header",
                locale
        );

        String body = localizationService.getLocalizedMessage(
                "message.welcome.body",
                locale
        );

        mailSender.send(user.email(),
                header,
                String.format(body, user.name())
        );
    }


}
