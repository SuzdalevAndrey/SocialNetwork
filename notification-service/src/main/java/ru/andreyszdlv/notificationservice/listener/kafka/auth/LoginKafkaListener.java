package ru.andreyszdlv.notificationservice.listener.kafka.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import org.springframework.kafka.annotation.KafkaListener;

import ru.andreyszdlv.notificationservice.dto.auth.LoginUserDTO;

@Component
@Slf4j
@AllArgsConstructor
public class LoginKafkaListener {

    private final ObjectMapper mapper;

    private final ApplicationEventPublisher publisher;

    @KafkaListener(
            topics = "#{@kafkaConsumerProperties.topicNameLoginUser}",
            groupId = "#{@kafkaConsumerProperties.groupId}"
    )
    public void listen(String message) throws JsonProcessingException {
        log.info("Executing listen message in kafka");

        LoginUserDTO user = mapper.readValue(message, LoginUserDTO.class);

        publisher.publishEvent(user);

        log.info("Login email: {}, name: {}", user.email(), user.name());
    }
}
