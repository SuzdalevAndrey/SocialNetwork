package ru.andreyszdlv.postservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import ru.andreyszdlv.postservice.dto.kafka.CreateCommentKafkaDTO;
import ru.andreyszdlv.postservice.dto.kafka.CreateLikeKafkaDTO;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class KafkaProducerServiceTest {

    @Mock
    KafkaTemplate<String, CreateLikeKafkaDTO> kafkaTemplateCreateLike;

    @Mock
    KafkaTemplate<String, CreateCommentKafkaDTO> kafkaTemplateCreateComment;

    @InjectMocks
    KafkaProducerService kafkaProducerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(kafkaProducerService, "nameTopicCreateLike", "test-create-like");
        ReflectionTestUtils.setField(kafkaProducerService, "nameTopicCreateComment", "test-create-comment");
    }

    @Test
    public void sendCreateLikeEvent_Success_WhenValidData() {
        String email = "email@email.com";
        String nameAuthorLike = "name";

        kafkaProducerService.sendCreateLikeEvent(email, nameAuthorLike);

        verify(kafkaTemplateCreateLike, times(1)).send(
                eq("test-create-like"),
                any(CreateLikeKafkaDTO.class)
        );
    }

    @Test
    public void sendCreateCommentEvent_Success_WhenValidData() {
        String email = "email@email.com";
        String nameCommentAuthor = "name";
        String content = "content";

        kafkaProducerService.sendCreateCommentEvent(email, nameCommentAuthor, content);

        verify(kafkaTemplateCreateComment, times(1)).send(
            eq("test-create-comment"),
            any(CreateCommentKafkaDTO.class)
        );
    }
}