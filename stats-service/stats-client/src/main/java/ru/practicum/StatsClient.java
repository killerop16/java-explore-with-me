package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.hit.HitRequest;
import ru.practicum.hit.HitResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsClient {
    private final WebClient webClient;

    public Mono<String> sendHit(HitRequest hitRequest) {
        return webClient.post()
                .uri("/hit")
                .bodyValue(hitRequest)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<List<HitResponse>> getStats(String start, String end, List<String> uris, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(HitResponse.class)
                .collectList();
    }
}