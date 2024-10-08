package ru.andreyszdlv.notificationservice.listener.notification.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.user.EditPasswordDTO;
import ru.andreyszdlv.notificationservice.service.LocalizationService;
import ru.andreyszdlv.notificationservice.service.MailSenderService;

import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailEditPasswordNotificationListener {

    private final LocalizationService localizationService;

    private final MailSenderService mailSender;

    @EventListener
    public void handle(EditPasswordDTO editPasswordDTO){
        Locale locale = Locale.getDefault();

        String header = localizationService.getLocalizedMessage(
                "message.edit_password.header",
                locale
        );

        String body = localizationService.getLocalizedMessage(
                "message.edit_password.body",
                locale
        );

        mailSender.send(
                editPasswordDTO.email(),
                header,
                body
        );
    }
}
