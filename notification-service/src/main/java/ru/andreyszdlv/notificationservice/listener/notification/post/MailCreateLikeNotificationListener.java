package ru.andreyszdlv.notificationservice.listener.notification.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.post.CreateLikeDTO;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailCreateLikeNotificationListener {
    private final MailSenderService mailSender;

    @Value("${message.createlike.header}")
    private String header;

    @Value("${message.createlike.body}")
    private String body;

    @EventListener
    public void handle(CreateLikeDTO createLikeDTO){
        mailSender.send(
                createLikeDTO.email(),
                header,
                String.format(body, createLikeDTO.nameLikeAuthor())
        );
    }
}
