package ru.andreyszdlv.postservice.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.andreyszdlv.postservice.client.UserServiceClient;
import ru.andreyszdlv.postservice.configuration.KafkaProducerConfig;
import ru.andreyszdlv.postservice.configuration.S3Config;
import ru.andreyszdlv.postservice.configuration.S3Initializer;
import ru.andreyszdlv.postservice.service.KafkaProducerService;
import ru.andreyszdlv.postservice.service.S3Service;

@SpringBootTest
abstract class BaseIT {

    @MockBean
    KafkaProducerService kafkaProducerService;

    @MockBean
    KafkaProducerConfig kafkaProducerConfig;

    @MockBean
    UserServiceClient userServiceClient;

    @MockBean
    S3Service s3Service;

    @MockBean
    S3Initializer s3Initializer;

    @MockBean
    S3Config s3Config;
}
