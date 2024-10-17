package ru.andreyszdlv.authservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.authservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.authservice.service.compensation.ConfirmEmailCompensationService;

@Component
@RequiredArgsConstructor
public class FailureSaveUserEventListener {

    private final ConfirmEmailCompensationService compensationService;

    private final ObjectMapper mapper;

    @KafkaListener(
            topics = "${spring.kafka.topic.name.failure-save-user}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String messageUser) throws JsonProcessingException {
        UserDetailsKafkaDTO user = mapper
                .readValue(messageUser, UserDetailsKafkaDTO.class);

        compensationService.handle(user.name(), user.email(), user.password(), user.role());
    }
}
