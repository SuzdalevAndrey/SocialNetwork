package ru.andreyszdlv.notificationservice.listener.notification.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.post.CreateCommentDTO;
import ru.andreyszdlv.notificationservice.service.LocalizationService;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailCreateCommentNotificationListener {

    private final LocalizationService localizationService;

    private final MailSenderService mailSender;

    @EventListener
    public void handle(CreateCommentDTO createCommentDTO){
        log.info("Executing handle create comment event for email author post: {}",
                createCommentDTO.email());

        Locale locale = Locale.getDefault();

        String header = localizationService.getLocalizedMessage(
                "message.create_comment.header",
                locale
        );

        String body = localizationService.getLocalizedMessage(
                "message.create_comment.body",
                locale
        );

        log.info("Sending create comment event email to email author post: {}",
                createCommentDTO.email());
        mailSender.send(
                createCommentDTO.email(),
                header,
                String.format(body, createCommentDTO.content())
        );
    }
}
