package ru.andreyszdlv.notificationservice.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public class KafkaConsumerProperties {

    private String topicNameRegisterUser;

    private String topicNameLoginUser;

    private String topicNameEditEmail;

    private String topicNameEditPassword;

    private String topicNameCreateLike;

    private String topicNameCreateComment;

    private String groupId;
}
