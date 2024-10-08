package ru.andreyszdlv.notificationservice.listener.notification.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.auth.LoginUserDTO;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

@Component
@RequiredArgsConstructor
public class MailWelcomeNotificationListener {

    private final MessageSource messageSource;

    @Value("${message.welcome.header}")
    private String welcomeHeader;

    @Value("${message.welcome.body}")
    private String welcomeBody;

    private final MailSenderService mailSender;

    @EventListener
    public void handle(LoginUserDTO user){

        mailSender.send(user.email(),
                welcomeHeader,
                String.format(welcomeBody, user.name())
        );
    }

}
