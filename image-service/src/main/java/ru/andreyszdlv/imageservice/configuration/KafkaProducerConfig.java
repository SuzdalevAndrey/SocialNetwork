package ru.andreyszdlv.imageservice.configuration;


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
import ru.andreyszdlv.imageservice.dto.kafka.SaveImageIdKafkaDTO;
import ru.andreyszdlv.imageservice.props.KafkaProducerProperties;

import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProducerProperties producerProperties;

    @Bean
    public ProducerFactory<String, SaveImageIdKafkaDTO> producerFactory() {
        HashMap<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    KafkaTemplate<String, SaveImageIdKafkaDTO> saveImageIdKafkaTemplate(
            ProducerFactory<String, SaveImageIdKafkaDTO> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic newTopicSaveImageId(){
        return TopicBuilder
                .name(producerProperties.getTopicNameSaveImageId())
                .partitions(1)
                .replicas(1)
                .build();
    }
}
