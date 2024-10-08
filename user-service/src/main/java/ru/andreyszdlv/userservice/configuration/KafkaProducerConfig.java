package ru.andreyszdlv.userservice.configuration;

import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.andreyszdlv.userservice.dto.kafkaDto.EditEmailKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafkaDto.EditPasswordKafkaDTO;

import java.util.HashMap;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${spring.kafka.topic.nameTopicEditEmail}")
    private String nameTopicEditEmail;

    @Value("${spring.kafka.topic.nameTopicEditPassword}")
    private String nameTopicEditPassword;

    @Bean
    public ProducerFactory<String, EditEmailKafkaDTO> editEmailProducerFactory() {
        HashMap<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, EditEmailKafkaDTO> editEmailKafkaTemplate(
            ProducerFactory<String, EditEmailKafkaDTO> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, EditPasswordKafkaDTO> editPasswordProducerFactory() {
        HashMap<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, EditPasswordKafkaDTO> editPasswordKafkaTemplate(
            ProducerFactory<String, EditPasswordKafkaDTO> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic newTopicEditEmail(){
        return TopicBuilder
                .name(nameTopicEditEmail)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic newTopicEditPassword(){
        return TopicBuilder
                .name(nameTopicEditPassword)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
