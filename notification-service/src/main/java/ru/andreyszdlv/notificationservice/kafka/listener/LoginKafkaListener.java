package ru.andreyszdlv.notificationservice.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import org.springframework.kafka.annotation.KafkaListener;

import ru.andreyszdlv.notificationservice.model.LoginUser;

@Component
@Slf4j
@AllArgsConstructor
public class LoginKafkaListener {

    private final ObjectMapper mapper;

    private final ApplicationEventPublisher publisher;

    @KafkaListener(topics = "auth-event-login", groupId = "notification-group")
    public void listen(String message) throws JsonProcessingException {
        LoginUser user = mapper.readValue(message, LoginUser.class);

        publisher.publishEvent(user);

        log.info("Login email: {}, name: {}", user.getEmail(), user.getName());
    }
}
