package ru.andreyszdlv.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;


class LocalizationServiceTest {

    @Mock
    MessageSource messageSource;

    @InjectMocks
    LocalizationService localizationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getLocalizedMessage_ReturnedLocalizationMessage_WhenValidData() {
        String code = "errors.400.request.title";
        Locale locale = new Locale("en", "US");

        localizationService.getLocalizedMessage(code, locale);

        verify(messageSource).getMessage(code, null, code, locale);
    }
}