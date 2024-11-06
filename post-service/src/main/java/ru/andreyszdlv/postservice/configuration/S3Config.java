package ru.andreyszdlv.postservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.andreyszdlv.postservice.props.S3Properties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    public S3Client s3Client() {
        return S3Client
                .builder()
                .endpointOverride(URI.create(s3Properties.getEndpoint()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                    s3Properties.getAccessKey(),
                                    s3Properties.getSecretKey()
                            )
                        )
                )
                .region(Region.AP_NORTHEAST_1)
                .serviceConfiguration(
                        S3Configuration
                                .builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(){
        return S3Presigner
                .builder()
                .endpointOverride(URI.create(s3Properties.getEndpoint()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                    s3Properties.getAccessKey(),
                                    s3Properties.getSecretKey()
                            )
                        )
                )
                .region(Region.AP_NORTHEAST_1)
                .serviceConfiguration(
                        S3Configuration
                                .builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }
}
