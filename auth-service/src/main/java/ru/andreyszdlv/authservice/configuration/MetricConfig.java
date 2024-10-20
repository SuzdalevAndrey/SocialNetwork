package ru.andreyszdlv.authservice.configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MetricConfig {
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public MeterBinder meterBinder(){
        return registry -> {
            Counter.builder("user_registry")
                    .description("Количество зарегистрированных пользователей")
                    .tags(List.of())
                    .register(registry);
        };
    }
}
