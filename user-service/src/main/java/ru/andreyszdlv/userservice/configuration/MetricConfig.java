package ru.andreyszdlv.userservice.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MetricConfig {

    @Bean
    public MeterBinder meterBinder(){
        return registry -> {
            Counter.builder("user_change_password")
                    .description("Количество изменений паролей")
                    .tags(List.of())
                    .register(registry);
        };
    }
}
