package ru.practicum.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.hit.HitRequest;
import ru.practicum.hit.HitResponse;
import ru.practicum.service.StatsService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHit(@Valid @RequestBody HitRequest hitRequest) {
        log.info("Creating hit for URI: {}", hitRequest.getUri());
        statsService.createHit(hitRequest);
    }

    @GetMapping("/stats")
    public List<HitResponse> findHits(
            @RequestParam(value = "start") @NotEmpty(message = "Start date cannot be empty") String start,
            @RequestParam(value = "end") @NotEmpty(message = "End date cannot be empty") String end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        log.info("Finding hits between {} and {}, with uris: {}", start, end, uris);
        return statsService.findHits(start, end, uris, unique);
    }
}
