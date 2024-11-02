package ru.andreyszdlv.imageservice.listener.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.imageservice.dto.kafka.FailureSaveImageIdKafkaDTO;
import ru.andreyszdlv.imageservice.service.ImageUserService;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailureSaveImageIdEventListener {

    private final ObjectMapper mapper;

    private final ImageUserService imageUserService;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topicNameFailureSaveImageId}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String message)
            throws Exception{
        log.info("Executing failure save image id event");

        log.info("Mapping String in FailureSaveImageIdKafkaDTO");
        FailureSaveImageIdKafkaDTO kafkaDTO = mapper.readValue(
                message,
                FailureSaveImageIdKafkaDTO.class
        );

        log.info("Deleting new image: {}", kafkaDTO.newImageId());
        imageUserService.deleteImage(kafkaDTO.newImageId());

    }
}
