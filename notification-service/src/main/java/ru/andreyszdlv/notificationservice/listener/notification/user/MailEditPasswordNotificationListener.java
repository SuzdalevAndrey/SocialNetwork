package ru.andreyszdlv.notificationservice.listener.notification.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.user.EditPasswordKafkaDTO;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailEditPasswordNotificationListener {

    @Value("${message.editpassword.header}")
    private String header;

    @Value("${message.editpassword.body}")
    private String body;

    private final MailSenderService mailSender;

    @EventListener
    public void handle(EditPasswordKafkaDTO editPasswordKafka){
        mailSender.send(
                editPasswordKafka.email(),
                header,
                body
        );
    }
}
