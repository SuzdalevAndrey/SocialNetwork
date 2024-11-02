package ru.andreyszdlv.imageservice.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public class KafkaConsumerProperties {

    private String bootstrapServers;

    private String topicNameFailureSaveImageId;

    private String topicNameSuccessSaveImageId;

    private String groupId;
}
