package ru.andreyszdlv.userservice.configuration;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.andreyszdlv.userservice.props.MinioProperties;

@Configuration
@RequiredArgsConstructor
public class MinIoConfig {

    private final MinioProperties minioProperties;

    @Bean
    MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }
}
