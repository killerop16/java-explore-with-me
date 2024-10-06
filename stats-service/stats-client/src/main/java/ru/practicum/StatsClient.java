package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.hit.HitRequest;
import ru.practicum.hit.HitResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsClient {
    private final WebClient webClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void sendHit(HitRequest hitRequest) {
        hitRequest.setApp("ewm-service");

        String formattedTimestamp = LocalDateTime.now().format(formatter);
        hitRequest.setTimestamp(formattedTimestamp);

        String response = webClient.post()
                .uri("/hit")
                .bodyValue(hitRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block();  // Синхронное ожидание ответа

        log.info("Response from server: {}", response);
    }

    public Mono<List<HitResponse>> getStats(String start, String end, List<String> uris, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start.formatted(formatter))
                        .queryParam("end", end.formatted(formatter))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(HitResponse.class)
                .collectList();
    }
}