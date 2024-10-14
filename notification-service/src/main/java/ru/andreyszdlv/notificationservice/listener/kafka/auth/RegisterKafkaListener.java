package ru.andreyszdlv.notificationservice.listener.kafka.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.auth.RegisterUserDTO;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterKafkaListener {

    private final ObjectMapper mapper;

    private final ApplicationEventPublisher publisher;

    @KafkaListener(
            topics = "${spring.kafka.topic.name.register-user}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String registerUser) throws JsonProcessingException {

        RegisterUserDTO user = mapper.readValue(registerUser, RegisterUserDTO.class);

        publisher.publishEvent(user);

        log.info("Registered user: " + user.email());
    }
}
