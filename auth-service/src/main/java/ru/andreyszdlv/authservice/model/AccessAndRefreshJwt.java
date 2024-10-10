package ru.andreyszdlv.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class AccessAndRefreshJwt {
    private String accessToken;
    private String refreshToken;
}
