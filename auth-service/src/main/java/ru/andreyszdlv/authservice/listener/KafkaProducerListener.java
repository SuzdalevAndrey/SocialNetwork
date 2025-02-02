package ru.andreyszdlv.authservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import ru.andreyszdlv.authservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.authservice.dto.kafka.LoginUserKafkaDTO;
import ru.andreyszdlv.authservice.dto.kafka.RegisterUserKafkaDTO;
import ru.andreyszdlv.authservice.props.KafkaProducerProperties;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class KafkaProducerListener {

    private final KafkaProducerProperties kafkaProducerProperties;

    private final KafkaTemplate<String, LoginUserKafkaDTO> kafkaTemplateLogin;

    private final KafkaTemplate<String, RegisterUserKafkaDTO> kafkaTemplateRegister;

    private final KafkaTemplate<String, UserDetailsKafkaDTO> kafkaTemplateSaveUser;

    @EventListener
    public void sendRegisterEvent(RegisterUserKafkaDTO registerUser) {
        log.info("Executing sendRegisterEvent in kafka with email: {} and verification code", registerUser.email());
        kafkaTemplateRegister.send(
                kafkaProducerProperties.getTopicNameRegisterUser(),
                registerUser);
    }

    @EventListener
    public void sendLoginEvent(LoginUserKafkaDTO loginUser){
        log.info("Executing sendLoginEvent in kafka with name: {}, email: {}", loginUser.name(), loginUser.email());
        kafkaTemplateLogin.send(
                kafkaProducerProperties.getTopicNameLoginUser(),
                loginUser
        );
    }

    @EventListener
    public void sendSaveUserEvent(UserDetailsKafkaDTO userDetails){
        log.info("Executing sendSaveUserEvent in kafka with userName: {}, userEmail: {}",
                userDetails.name(),
                userDetails.email()
        );
        kafkaTemplateSaveUser.send(
                kafkaProducerProperties.getTopicNameSaveUser(),
                UserDetailsKafkaDTO
                        .builder()
                        .messageId(UUID.randomUUID())
                        .name(userDetails.name())
                        .email(userDetails.email())
                        .password(userDetails.password())
                        .role(userDetails.role())
                        .build()
        );
    }
}
