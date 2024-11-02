package ru.andreyszdlv.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProblemDetailService {

    private final LocalizationService localizationService;

    public ProblemDetail createProblemDetail(HttpStatus status, String message, Locale locale){
        log.info("Executing createProblemDetail for message {}", message);

        return ProblemDetail.forStatusAndDetail(
                status,
                localizationService.getLocalizedMessage(
                        message,
                        locale
                )
        );
    }
}
