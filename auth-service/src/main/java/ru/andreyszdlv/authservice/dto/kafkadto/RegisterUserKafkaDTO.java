package ru.andreyszdlv.authservice.dto.kafkadto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterUserKafkaDTO {

    private String email;

    private String code;
}
