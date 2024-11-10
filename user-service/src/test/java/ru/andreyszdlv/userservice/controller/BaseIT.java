package ru.andreyszdlv.userservice.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.andreyszdlv.userservice.configuration.KafkaConsumerConfig;
import ru.andreyszdlv.userservice.configuration.KafkaProducerConfig;
import ru.andreyszdlv.userservice.configuration.S3Config;
import ru.andreyszdlv.userservice.configuration.S3Initializer;
import ru.andreyszdlv.userservice.listener.SaveUserEventListener;
import ru.andreyszdlv.userservice.service.KafkaProducerService;
import ru.andreyszdlv.userservice.service.S3Service;

@SpringBootTest
public abstract class BaseIT {
    @MockBean
    KafkaProducerService kafkaProducerService;

    @MockBean
    SaveUserEventListener saveUserEventListener;

    @MockBean
    KafkaConsumerConfig kafkaConsumerConfig;

    @MockBean
    KafkaProducerConfig kafkaProducerConfig;

    @MockBean
    S3Initializer s3Initializer;

    @MockBean
    S3Service s3Service;

    @MockBean
    S3Config s3Config;
}