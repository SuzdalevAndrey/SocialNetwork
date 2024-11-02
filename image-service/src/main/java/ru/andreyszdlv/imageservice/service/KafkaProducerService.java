package ru.andreyszdlv.imageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.imageservice.dto.kafka.SaveImageIdKafkaDTO;
import ru.andreyszdlv.imageservice.props.KafkaProducerProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaProducerProperties producerProperties;

    private final KafkaTemplate<String, SaveImageIdKafkaDTO> saveImageIdKafkaTemplate;

    public void sendSaveImageIdEvent(long userId, String imageId){
        log.info("Executing sendSaveImageIdEvent in kafka for userId: {}, imageId: {}",
                userId,
                imageId
        );
        saveImageIdKafkaTemplate.send(
                producerProperties.getTopicNameSaveImageId(),
                SaveImageIdKafkaDTO
                        .builder()
                        .userId(userId)
                        .imageId(imageId)
                        .build()
        );
    }
}
