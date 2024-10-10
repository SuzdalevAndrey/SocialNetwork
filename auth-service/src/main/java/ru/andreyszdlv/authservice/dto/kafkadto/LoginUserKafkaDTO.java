package ru.andreyszdlv.authservice.dto.kafkadto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserKafkaDTO {
    private String name;

    private String email;
}
