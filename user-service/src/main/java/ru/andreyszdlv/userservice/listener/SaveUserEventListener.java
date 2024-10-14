package ru.andreyszdlv.userservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.userservice.dto.kafkaDto.UserDetailsKafkaDTO;
import ru.andreyszdlv.userservice.service.KafkaProducerService;
import ru.andreyszdlv.userservice.service.UserService;

@Component
@RequiredArgsConstructor
public class SaveUserEventListener {

    private final ObjectMapper mapper;

    private final UserService userService;

    @KafkaListener(topics = "${spring.kafka.topic.name.save-user}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String messageUser)
            throws JsonProcessingException {

        UserDetailsKafkaDTO user = mapper.readValue(messageUser, UserDetailsKafkaDTO.class);

        userService.saveUser(
                user.name(),
                user.email(),
                user.password(),
                user.role()
        );
    }
}
