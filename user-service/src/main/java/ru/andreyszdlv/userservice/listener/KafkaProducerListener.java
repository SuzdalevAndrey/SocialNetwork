package ru.andreyszdlv.userservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import ru.andreyszdlv.userservice.dto.kafka.EditEmailKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.EditPasswordKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.userservice.props.KafkaProducerProperties;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class KafkaProducerListener {

    private final KafkaProducerProperties producerProperties;

    private final KafkaTemplate<String, EditEmailKafkaDTO> kafkaTemplateEditEmail;

    private final KafkaTemplate<String, EditPasswordKafkaDTO> kafkaTemplateEditPassword;

    private final KafkaTemplate<String, UserDetailsKafkaDTO> kafkaTemplateFailureSaveUser;

    @EventListener
    public void sendEditEmailEvent(EditEmailKafkaDTO editEmailDTO){
        log.info("Executing sendEditEmailEvent in kafka with oldEmail");
        kafkaTemplateEditEmail.send(
                producerProperties.getTopicNameEditEmail(),
                editEmailDTO
        );
    }

    @EventListener
    public void sendEditPasswordEvent(EditPasswordKafkaDTO editPasswordDTO){
        log.info("Executing sendEditPasswordEvent in kafka");
        kafkaTemplateEditPassword.send(
                producerProperties.getTopicNameEditPassword(),
                editPasswordDTO
        );
    }

    @EventListener
    public void sendFailureSaveUserEvent(UserDetailsKafkaDTO userDetailsDTO){
        log.info("Executing sendFailureSaveUserEvent in kafka with name, email: {}, password, role",
                userDetailsDTO.email()
        );
        kafkaTemplateFailureSaveUser.send(
                producerProperties.getTopicNameFailureSaveUser(),
                UserDetailsKafkaDTO
                        .builder()
                        .messageId(UUID.randomUUID())
                        .name(userDetailsDTO.name())
                        .email(userDetailsDTO.email())
                        .password(userDetailsDTO.password())
                        .role(userDetailsDTO.role())
                        .build()
        );
    }
}
