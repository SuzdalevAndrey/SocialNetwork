package ru.andreyszdlv.imageservice.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.producer")
public class KafkaProducerProperties {

    private String bootstrapServers;

    private String topicNameSaveImageId;
}
