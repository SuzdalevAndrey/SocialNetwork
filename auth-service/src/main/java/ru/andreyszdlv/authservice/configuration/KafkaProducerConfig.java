package ru.andreyszdlv.authservice.configuration;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.andreyszdlv.authservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.authservice.dto.kafka.RegisterUserKafkaDTO;
import ru.andreyszdlv.authservice.dto.kafka.LoginUserKafkaDTO;
import ru.andreyszdlv.authservice.props.KafkaProducerProperties;

import java.util.HashMap;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProducerProperties kafkaProducerProperties;

    @Bean
    public ProducerFactory<String, LoginUserKafkaDTO> LoginUserProducerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, LoginUserKafkaDTO> kafkaTemplateLoginUser(
            ProducerFactory<String, LoginUserKafkaDTO> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, RegisterUserKafkaDTO> RegisterUserProducerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, RegisterUserKafkaDTO> kafkaTemplateRegisterUser(
            ProducerFactory<String, RegisterUserKafkaDTO> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, UserDetailsKafkaDTO> SaveUserProducerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, UserDetailsKafkaDTO> kafkaTemplateSaveUser(
            ProducerFactory<String, UserDetailsKafkaDTO> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic newTopicLogin() {
        return TopicBuilder
                .name(kafkaProducerProperties.getTopicNameLoginUser())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic newTopicRegister() {
        return TopicBuilder
                .name(kafkaProducerProperties.getTopicNameRegisterUser())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic newTopicSaveUser() {
        return TopicBuilder
                .name(kafkaProducerProperties.getTopicNameSaveUser())
                .partitions(1)
                .replicas(1)
                .build();
    }
}
