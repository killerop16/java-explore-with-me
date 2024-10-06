package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${stats-server.url}") // URL внешнего сервиса
    private String statsServerUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(statsServerUrl)
                .build();
    }
}

