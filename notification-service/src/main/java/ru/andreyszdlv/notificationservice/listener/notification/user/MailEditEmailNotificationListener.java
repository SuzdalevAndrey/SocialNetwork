package ru.andreyszdlv.notificationservice.listener.notification.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.user.EditEmailKafkaDTO;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

@Component
@RequiredArgsConstructor
public class MailEditEmailNotificationListener {

    private final MailSenderService mailSender;

    @Value("${message.editemail.header}")
    private String header;

    @Value("${message.editemail.body}")
    private String body;

    @EventListener
    public void handle(EditEmailKafkaDTO editEmailKafka){
        mailSender.send(
                editEmailKafka.newEmail(),
                header,
                String.format(body, editEmailKafka.oldEmail())
        );
    }
}
