package ru.andreyszdlv.notificationservice.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.model.RegisterUser;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterKafkaListener {

    private final ObjectMapper mapper;

    private final ApplicationEventPublisher publisher;

    @KafkaListener(topics = "auth-event-register", groupId = "notification-group")
    public void listen(String registerUser) throws JsonProcessingException {

        RegisterUser user = mapper.readValue(registerUser, RegisterUser.class);

        publisher.publishEvent(user);

        log.info("Registered user: " + user.getEmail());
    }
}
