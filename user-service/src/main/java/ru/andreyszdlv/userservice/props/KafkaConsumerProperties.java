package ru.andreyszdlv.userservice.props;

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

    private String topicNameSaveUser;

    private String groupId;
}