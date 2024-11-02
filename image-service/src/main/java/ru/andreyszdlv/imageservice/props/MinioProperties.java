package ru.andreyszdlv.imageservice.props;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    String endpoint;
    String accessKey;
    String secretKey;
    String bucketUserAvatar;
    String bucketPost;
}
