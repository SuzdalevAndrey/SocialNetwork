package ru.andreyszdlv.userservice.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.userservice.props.S3Properties;
import software.amazon.awssdk.services.s3.S3Client;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Initializer {

    private final S3Properties s3Properties;

    private final S3Client s3Client;

    @PostConstruct
    public void init() {
        String bucketName = s3Properties.getBucketUserAvatar();

        log.info("Creating bucket for bucketName: {}", bucketName);

        boolean found = s3Client.listBuckets()
                .buckets()
                .stream()
                .anyMatch(
                        b -> b.name().equals(bucketName)
                );

        log.info("Check bucket exists for {}, found: {}", bucketName, found);
        if (!found) {
            log.info("Bucket not exists, creating bucket for name: {}", bucketName);
            s3Client.createBucket(b -> b.bucket(bucketName));
        }
    }
}