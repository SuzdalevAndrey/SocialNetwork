package ru.andreyszdlv.notificationservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.andreyszdlv.notificationservice.dto.auth.FailureSendRegisterMailDTO;

import java.util.HashMap;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${spring.kafka.topic.name.failure-send-register-mail}")
    private String nameTopicFailureSendRegisterMail;

    @Bean
    public ProducerFactory<String, FailureSendRegisterMailDTO> producerFactory(){
        HashMap<String, Object> props = new HashMap<>(3);

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, FailureSendRegisterMailDTO> kafkaTemplateRegisterCompensation(
            ProducerFactory<String, FailureSendRegisterMailDTO> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic newTopicRegisterCompensation(){
        return TopicBuilder
                .name(nameTopicFailureSendRegisterMail)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
