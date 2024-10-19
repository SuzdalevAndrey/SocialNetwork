package ru.andreyszdlv.notificationservice.listener.notification.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.user.EditEmailDTO;
import ru.andreyszdlv.notificationservice.service.LocalizationService;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailEditEmailNotificationListener {
    private final LocalizationService localizationService;

    private final MailSenderService mailSender;

    @EventListener
    public void handle(EditEmailDTO editEmailDTO){
        log.info("Executing handle edit email event for user: {}",
                editEmailDTO.newEmail());

        Locale locale = Locale.getDefault();

        String header = localizationService.getLocalizedMessage(
                "message.edit_email.header",
                locale
        );

        String body = localizationService.getLocalizedMessage(
                "message.edit_email.body",
                locale
        );

        log.info("Sending edit email event for user: {}", editEmailDTO.newEmail());
        mailSender.send(
                editEmailDTO.newEmail(),
                header,
                String.format(body, editEmailDTO.oldEmail())
        );
    }
}
