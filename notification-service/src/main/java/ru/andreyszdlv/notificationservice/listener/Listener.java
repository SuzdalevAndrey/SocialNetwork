package ru.andreyszdlv.notificationservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import org.springframework.kafka.annotation.KafkaListener;
import ru.andreyszdlv.notificationservice.model.UserModel;

@Component
@Slf4j
@AllArgsConstructor
public class Listener {

    private final ObjectMapper jacksonObjectMapper;

    @KafkaListener(topics = "auth-event", groupId = "notification-group")
    public void listenRegister(String message) throws JsonProcessingException {
        UserModel userModel = jacksonObjectMapper.readValue(message, UserModel.class);
        log.info("Register email: {}, name: {}", userModel.getEmail(), userModel.getName());
    }

    @KafkaListener(topics = "auth-event", groupId = "notification-group")
    public void listenLogin(String message) throws JsonProcessingException {
        UserModel userModel = jacksonObjectMapper.readValue(message, UserModel.class);
        log.info("Login email: {}, name: {}", userModel.getEmail(), userModel.getName());

    }
}
