package ru.andreyszdlv.userservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.userservice.dto.kafka.SaveImageIdKafkaDTO;
import ru.andreyszdlv.userservice.service.InternalUserService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveImageIdEventListener {

    private final ObjectMapper mapper;

    private final InternalUserService internalUserService;

    @KafkaListener(
            topics = "#{@kafkaConsumerProperties.topicNameSaveImageId}",
            groupId = "#{@kafkaConsumerProperties.groupId}"
    )
    public void listen(String message)
            throws JsonProcessingException {
        log.info("Executing save image id event");

        log.info("Mapping String in SaveImageIdKafkaDTO");
        SaveImageIdKafkaDTO kafkaDTO = mapper.readValue(message, SaveImageIdKafkaDTO.class);

        log.info("Saving image id for userId: {}, imageId: {}",
                kafkaDTO.userId(),
                kafkaDTO.imageId()
        );
        internalUserService.saveImageId(kafkaDTO.userId(), kafkaDTO.imageId());
    }
}
