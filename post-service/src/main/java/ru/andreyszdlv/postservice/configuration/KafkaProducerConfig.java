package ru.andreyszdlv.postservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.andreyszdlv.postservice.dto.kafka.CreateCommentKafkaDTO;
import ru.andreyszdlv.postservice.dto.kafka.CreateLikeKafkaDTO;

import java.util.HashMap;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${spring.kafka.producer.topic.name.create-like}")
    private String nameTopicCreateLike;

    @Value("${spring.kafka.producer.topic.name.create-comment}")
    private String nameTopicCreateComment;

    @Bean
    public ProducerFactory<String, CreateLikeKafkaDTO> createLikeProducerFactory(){
        HashMap<String, Object> props = new HashMap<>(3);

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, CreateLikeKafkaDTO> createLikeKafkaTemplate(
            ProducerFactory<String, CreateLikeKafkaDTO> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, CreateCommentKafkaDTO> createCommentProducerFactory(){
        HashMap<String, Object> props = new HashMap<>(3);

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, CreateCommentKafkaDTO> createCommentKafkaTemplate(
            ProducerFactory<String, CreateCommentKafkaDTO> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic newTopicCreateLike(){
        return TopicBuilder
                .name(nameTopicCreateLike)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic newTopicCreateComment(){
        return TopicBuilder
                .name(nameTopicCreateComment)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
