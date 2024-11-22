package ru.andreyszdlv.authservice.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public class KafkaConsumerProperties {

    private String topicNameFailureSendRegisterMail;

    private String topicNameFailureSaveUser;

    private String groupId;
}
