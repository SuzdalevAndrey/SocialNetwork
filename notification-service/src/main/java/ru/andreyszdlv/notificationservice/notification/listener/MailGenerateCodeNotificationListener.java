package ru.andreyszdlv.notificationservice.notification.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.model.RegisterUser;
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
    public void handle(RegisterUser user){

        mailSender.send(
                user.getEmail(),
                header,
                String.format(body, user.getCode())
        );


    }
}
