package ru.andreyszdlv.notificationservice.listener.notification.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.post.CreateCommentDTO;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailCreateCommentNotificationListener {
    private final MailSenderService mailSender;

    @Value("${message.createcomment.header}")
    private String header;

    @Value("${message.createcomment.body}")
    private String body;

    @EventListener
    public void handle(CreateCommentDTO createCommentDTO){
        mailSender.send(
                createCommentDTO.email(),
                String.format(header, createCommentDTO.nameCommentAuthor()),
                String.format(body,createCommentDTO.content())
        );
    }
}
