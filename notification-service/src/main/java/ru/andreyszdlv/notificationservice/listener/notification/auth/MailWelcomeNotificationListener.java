package ru.andreyszdlv.notificationservice.listener.notification.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MailWelcomeNotificationListener {
    private final LocalizationService localizationService;

    private final MailSenderService mailSender;

    @EventListener
    public void handle(LoginUserDTO user){
        log.info("Executing handle login user event for email: {}", user.email());

        Locale locale = Locale.getDefault();

        String header = localizationService.getLocalizedMessage(
                "message.welcome.header",
                locale
        );

        String body = localizationService.getLocalizedMessage(
                "message.welcome.body",
                locale
        );

        log.info("Sending welcome email to user: {}", user.email());
        mailSender.send(user.email(),
                header,
                String.format(body, user.name())
        );
    }
}
