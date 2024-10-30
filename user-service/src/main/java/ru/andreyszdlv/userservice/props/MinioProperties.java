package ru.andreyszdlv.userservice.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    String endpoint;
    String accessKey;
    String secretKey;
    String bucket;
}
