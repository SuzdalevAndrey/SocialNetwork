package ru.andreyszdlv.authservice.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.producer")
@Component
public class KafkaProducerProperties {

    private String bootstrapServers;

    private String topicNameSaveUser;

    private String topicNameLoginUser;

    private String topicNameRegisterUser;
}
