package ru.andreyszdlv.imageservice.listener.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.imageservice.dto.kafka.SuccessSaveImageIdKafkaDTO;
import ru.andreyszdlv.imageservice.service.ImageUserService;

@Component
@Slf4j
@RequiredArgsConstructor
public class SuccessSaveImageIdEventListener {

    private final ObjectMapper mapper;

    private final ImageUserService imageUserService;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topicNameSuccessSaveImageId}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String message)
            throws Exception{
        log.info("Executing success save image id event");

        log.info("Mapping String in SuccessSaveImageIdKafkaDTO");
        SuccessSaveImageIdKafkaDTO kafkaDTO = mapper.readValue(
                message,
                SuccessSaveImageIdKafkaDTO.class
        );

        log.info("Deleting old image: {}", kafkaDTO.oldImageId());
        imageUserService.deleteImage(kafkaDTO.oldImageId());
    }
}
