package ru.andreyszdlv.authservice.configuration;


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
import ru.andreyszdlv.authservice.dto.kafkadto.UserDetailsKafkaDTO;
import ru.andreyszdlv.authservice.dto.kafkadto.RegisterUserKafkaDTO;
import ru.andreyszdlv.authservice.dto.kafkadto.LoginUserKafkaDTO;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${spring.kafka.topic.name.login-user}")
    private String nameTopicLoginUser;

    @Value("${spring.kafka.topic.name.register-user}")
    private String nameTopicRegisterUser;

    @Value("${spring.kafka.topic.name.save-user}")
    private String nameTopicSaveUser;

    @Bean
    public ProducerFactory<String, LoginUserKafkaDTO> LoginUserProducerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
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

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
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

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
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
                .name(nameTopicLoginUser)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic newTopicRegister() {
        return TopicBuilder
                .name(nameTopicRegisterUser)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic newTopicSaveUser() {
        return TopicBuilder
                .name(nameTopicSaveUser)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
