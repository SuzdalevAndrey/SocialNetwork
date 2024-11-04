package ru.andreyszdlv.userservice.props;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class S3Properties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketUserAvatar;
}
