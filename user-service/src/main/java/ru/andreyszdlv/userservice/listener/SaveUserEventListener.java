package ru.andreyszdlv.userservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.userservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.userservice.service.UserService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveUserEventListener {

    private final ObjectMapper mapper;

    private final UserService userService;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topic.name.save-user}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String messageUser)
            throws JsonProcessingException {
        log.info("Executing save user event");

        log.info("Mapping String in UserDetailsKafkaDTO");
        UserDetailsKafkaDTO user = mapper.readValue(messageUser, UserDetailsKafkaDTO.class);

        log.info("Saving user with email: {}", user.email());
        userService.saveUser(
                user.name(),
                user.email(),
                user.password(),
                user.role()
        );
    }
}
