package ru.andreyszdlv.notificationservice.listener.notification.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.post.CreateLikeDTO;
import ru.andreyszdlv.notificationservice.service.LocalizationService;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailCreateLikeNotificationListener {

    private final LocalizationService localizationService;

    private final MailSenderService mailSender;

    @EventListener
    public void handle(CreateLikeDTO createLikeDTO){
        log.info("Executing handle create like event for email author post: {}",
                createLikeDTO.email());

        Locale locale = Locale.getDefault();

        String header = localizationService.getLocalizedMessage(
                "message.create_like.header",
                locale
        );

        String body = localizationService.getLocalizedMessage(
                "message.create_like.body",
                locale
        );

        log.info("Sending create like event to email author post: {}", createLikeDTO.email());
        mailSender.send(
                createLikeDTO.email(),
                header,
                String.format(body, createLikeDTO.nameLikeAuthor())
        );
    }
}
