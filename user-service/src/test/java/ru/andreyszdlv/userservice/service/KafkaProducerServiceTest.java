package ru.andreyszdlv.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import ru.andreyszdlv.userservice.dto.kafka.EditEmailKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.EditPasswordKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.userservice.enums.ERole;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.endsWith;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaProducerServiceTest {

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

        ReflectionTestUtils.setField(kafkaProducerService, "nameTopicEditEmail", "test-edit-email");
        ReflectionTestUtils.setField(kafkaProducerService, "nameTopicEditPassword", "test-edit-password");
        ReflectionTestUtils.setField(kafkaProducerService, "nameTopicFailureSaveUser", "test-failure-save-user");
    }

    @Test
    public void sendEditEmailEvent_Success_WhenValidData() {
        String oldEmail = "oldEmail";
        String newEmail = "newEmail";
        EditEmailKafkaDTO expectedMessage = new EditEmailKafkaDTO(oldEmail, newEmail);

        kafkaProducerService.sendEditEmailEvent(oldEmail, newEmail);

        verify(kafkaTemplateEditEmail, times(1)).send(eq("test-edit-email"), any());
    }

    @Test
    public void sendEditPasswordEvent_Success_WhenValidData(){
        String email = "test@example.com";
        EditPasswordKafkaDTO expectedMessage = new EditPasswordKafkaDTO(email);

        kafkaProducerService.sendEditPasswordEvent(email);

        verify(kafkaTemplateEditPassword, times(1)).send(eq("test-edit-password"), any());
    }

    @Test
    public void sendFailureSaveUserEvent_Success_WhenValidData(){
        String name = "name";
        String email = "test@example.com";
        String password = "password";
        ERole role = ERole.USER;

        kafkaProducerService.sendFailureSaveUserEvent(name,email,password,role);

        verify(kafkaTemplateFailureSave, times(1)).send(
                eq("test-failure-save-user"),
                any()
        );
    }
}
