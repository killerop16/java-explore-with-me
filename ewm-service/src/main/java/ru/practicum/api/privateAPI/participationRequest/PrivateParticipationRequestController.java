package ru.practicum.api.privateAPI.participationRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.privateAPI.participationRequest.service.PrivateParticipationRequestService;
import ru.practicum.model.participationRequest.dto.ParticipationResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Validated
@Slf4j
public class PrivateParticipationRequestController {
    private final PrivateParticipationRequestService privateParticipationRequestService;

    @GetMapping
    public List<ParticipationResponseDto> findParticipationRequests(@PathVariable @Positive Long userId) {
        log.info("Получение запросов на участие для пользователя с id {}", userId);
        List<ParticipationResponseDto> requests = privateParticipationRequestService.findParticipationRequests(userId);
        log.info("Найдено {} запросов на участие для пользователя с id {}", requests.size(), userId);
        return requests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationResponseDto createParticipationRequest(@PathVariable @Positive Long userId,
                                                               @RequestParam @NotNull Long eventId) {
        log.info("Создание запроса на участие в событии с id {} для пользователя с id {}", eventId, userId);
        ParticipationResponseDto response = privateParticipationRequestService.createParticipationRequest(userId, eventId);
        log.info("Запрос на участие создан: {}", response);
        return response;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationResponseDto updateParticipationRequest(@PathVariable @Positive Long userId,
                                                               @PathVariable @Positive Long requestId) {
        log.info("Отмена запроса на участие с id {} для пользователя с id {}", requestId, userId);
        ParticipationResponseDto response = privateParticipationRequestService.updateParticipationRequest(userId, requestId);
        log.info("Запрос на участие с id {} для пользователя с id {} успешно отменен", requestId, userId);
        return response;
    }
}