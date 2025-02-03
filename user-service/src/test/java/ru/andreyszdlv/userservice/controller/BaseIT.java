package ru.andreyszdlv.userservice.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.andreyszdlv.userservice.configuration.KafkaConsumerConfig;
import ru.andreyszdlv.userservice.configuration.KafkaProducerConfig;
import ru.andreyszdlv.springbootstarters3loadimage.config.S3Initializer;
import ru.andreyszdlv.userservice.listener.SaveUserEventListener;
import ru.andreyszdlv.userservice.listener.KafkaProducerListener;
import ru.andreyszdlv.springbootstarters3loadimage.service.S3Service;

@SpringBootTest
abstract class BaseIT {
    @MockBean
    KafkaProducerListener kafkaProducerListener;

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
}