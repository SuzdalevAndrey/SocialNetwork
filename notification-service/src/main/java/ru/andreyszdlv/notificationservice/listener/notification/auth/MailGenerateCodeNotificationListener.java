package ru.andreyszdlv.notificationservice.listener.notification.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.auth.RegisterUserDTO;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

@Component
@RequiredArgsConstructor
public class MailGenerateCodeNotificationListener {

    @Value("${message.register.header}")
    private String header;

    @Value("${message.register.body}")
    private String body;

    private final MailSenderService mailSender;

    @EventListener
    public void handle(RegisterUserDTO user){

        mailSender.send(
                user.email(),
                header,
                String.format(body, user.code())
        );
    }
}
