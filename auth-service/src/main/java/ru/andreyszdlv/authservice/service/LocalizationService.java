package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalizationService {

    private final MessageSource messageSource;

    public String getLocalizedMessage(String code, Locale locale){
        return messageSource.getMessage(code, null, code, locale);
    }
}
