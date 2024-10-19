package ru.andreyszdlv.notificationservice.listener.kafka.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.user.EditEmailDTO;

@Component
@Slf4j
@RequiredArgsConstructor
public class EditEmailKafkaListener {

    private final ObjectMapper mapper;

    private final ApplicationEventPublisher publisher;

    @KafkaListener(topics = "${spring.kafka.consumer.topic.name.edit-email}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String editEmailMessage)
            throws JsonProcessingException {
        log.info("Executing listen message in kafka");

        EditEmailDTO editEmail = mapper.readValue(
                editEmailMessage,
                EditEmailDTO.class);

        publisher.publishEvent(editEmail);

        log.info("Edit oldEmail: {} on newEmail: {}",
                editEmail.oldEmail(),
                editEmail.newEmail()
        );
    }
}
