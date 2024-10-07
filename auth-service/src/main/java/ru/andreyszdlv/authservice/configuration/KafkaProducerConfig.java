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
import ru.andreyszdlv.authservice.model.RegisterUser;
import ru.andreyszdlv.authservice.model.LoginUser;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String kafkaBootstrapServers;


    @Bean
    public ProducerFactory<String, LoginUser> LoginUserProducerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, LoginUser> kafkaTemplateLoginUser(
            ProducerFactory<String, LoginUser> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, RegisterUser> RegisterUserProducerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, RegisterUser> kafkaTemplateRegisterUser(
            ProducerFactory<String, RegisterUser> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic newTopicLogin() {
        return TopicBuilder
                .name("auth-event-login")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic newTopicRegister() {
        return TopicBuilder
                .name("auth-event-register")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
