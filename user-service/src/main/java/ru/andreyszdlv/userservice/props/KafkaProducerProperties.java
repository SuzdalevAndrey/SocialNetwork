package ru.andreyszdlv.userservice.props;

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

    private String topicNameEditEmail;

    private String topicNameEditPassword;

    private String topicNameFailureSaveUser;

    private String topicNameFailureSaveImageId;

    private String topicNameSuccessSaveImageId;
}
