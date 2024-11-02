package ru.andreyszdlv.imageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProblemDetailService {

    private final LocalizationService localizationService;

    public ProblemDetail createProblemDetail(HttpStatus status,
                                             String message,
                                             Locale locale) {
        log.info("Executing createProblemDetail");
        return ProblemDetail.forStatusAndDetail(
                status,
                Optional.ofNullable(
                        localizationService.getLocalizedMessage(
                                message,
                                locale
                        )
                ).orElse("errors")
        );
    }

}
