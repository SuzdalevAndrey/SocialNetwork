package ru.andreyszdlv.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import ru.andreyszdlv.userservice.dto.kafka.EditEmailKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.EditPasswordKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.UserDetailsKafkaDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KafkaProducerServiceTest {
    @Value("${spring.kafka.producer.topic.name.edit-email}")
    String nameTopicEditEmail;

    @Value("${spring.kafka.producer.topic.name.edit-password}")
    String nameTopicEditPassword;

    @Value("${spring.kafka.producer.topic.name.failure-save-user}")
    String nameTopicFailureSaveUser;

    @Mock
    KafkaTemplate<String, EditEmailKafkaDTO> kafkaTemplateEditEmail;

    @Mock
    KafkaTemplate<String, EditPasswordKafkaDTO> kafkaTemplateEditPassword;

    @Mock
    KafkaTemplate<String, UserDetailsKafkaDTO> kafkaTemplateFailureSave;

    @InjectMocks
    KafkaProducerService kafkaProducerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void sendEditEmailEvent_Success_WhenValidData() {
        String oldEmail = "oldEmail";
        String newEmail = "newEmail";
        EditEmailKafkaDTO expectedMessage = new EditEmailKafkaDTO(oldEmail, newEmail);

        kafkaProducerService.sendEditEmailEvent(oldEmail, newEmail);

        verify(kafkaTemplateEditEmail).send(eq(nameTopicEditEmail), eq(expectedMessage));
    }

    @Test
    public void sendEditPasswordEvent_Success_WhenValidData(){

        kafkaProducerService.sendEditPasswordEvent(any());

        verify(kafkaTemplateEditPassword, times(1)).send(any(), any());
    }

    @Test
    public void sendFailureSaveUserEvent_Success_WhenValidData(){
        kafkaProducerService.sendFailureSaveUserEvent(any(), any(), any(), any());

        verify(kafkaTemplateFailureSave, times(1)).send(any(), any());
    }
}
