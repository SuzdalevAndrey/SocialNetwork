package ru.andreyszdlv.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ProblemDetailServiceTest {

    @Mock
    LocalizationService localizationService;

    @InjectMocks
    ProblemDetailService problemDetailService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProblemDetail_Success(){
        String code = "code";
        String message = "message";
        Locale locale = Locale.getDefault();
        when(localizationService.getLocalizedMessage(code,locale)).thenReturn(message);

        ProblemDetail response = problemDetailService.createProblemDetail(
                HttpStatus.NOT_FOUND,
                code,
                locale
        );

        assertNotNull(response);
        assertEquals(response.getStatus(), 404);
        assertEquals(response.getDetail(), message);
    }
}