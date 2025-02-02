package ru.andreyszdlv.postservice.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.producer")
@Component
public class KafkaProducerProperties {

    private String topicNameCreateLike;

    private String topicNameCreateComment;
}
