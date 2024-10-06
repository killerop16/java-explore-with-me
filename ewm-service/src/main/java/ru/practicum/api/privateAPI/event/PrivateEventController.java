package ru.practicum.api.privateAPI.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.privateAPI.event.service.PrivateEventService;
import ru.practicum.model.event.dto.CreateEventDto;
import ru.practicum.model.event.dto.EventFullResponseDto;
import ru.practicum.model.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.model.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.model.event.dto.EventShortResponseDto;
import ru.practicum.model.event.dto.UpdateEventUserRequest;
import ru.practicum.model.participationRequest.dto.ParticipationResponseDto;
import ru.practicum.utils.Constants;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
@Slf4j
public class PrivateEventController {
    private final PrivateEventService privateEventService;

    @GetMapping
    public List<EventShortResponseDto> getUserEvents(@PathVariable @Positive Long userId,
                                                     @PositiveOrZero
                                                     @RequestParam(defaultValue = Constants.ZERO) Integer from,
                                                     @Positive
                                                     @RequestParam(defaultValue = Constants.TEN) Integer size) {
        log.info("Getting events for user with ID: {}, from: {}, size: {}", userId, from, size);
        List<EventShortResponseDto> events = privateEventService.getUserEvents(userId, from, size);
        log.info("Retrieved {} events for user with ID: {}", events.size(), userId);
        return events;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullResponseDto addEvent(@PathVariable @Positive Long userId,
                                         @RequestBody @Valid CreateEventDto newEventDto) {
        log.info("Adding new event for user with ID: {}", userId);
        EventFullResponseDto createdEvent = privateEventService.addEvent(userId, newEventDto);
        log.info("Event created with ID: {} for user with ID: {}", createdEvent.getId(), userId);
        return createdEvent;
    }

    @GetMapping("/{eventId}")
    public EventFullResponseDto getEventById(@PathVariable @Positive Long userId,
                                             @PathVariable @Positive Long eventId) {
        log.info("Getting event with ID: {} for user with ID: {}", eventId, userId);
        EventFullResponseDto event = privateEventService.getEventById(userId, eventId);
        log.info("Retrieved event with ID: {} for user with ID: {}", eventId, userId);
        return event;
    }

    @PatchMapping("/{eventId}")
    public EventFullResponseDto updateEvent(@PathVariable @Positive Long userId,
                                            @PathVariable @Positive Long eventId,
                                            @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Updating event with ID: {} for user with ID: {}", eventId, userId);
        EventFullResponseDto updatedEvent = privateEventService.updateEvent(userId, eventId, updateEventUserRequest);
        log.info("Updated event with ID: {} for user with ID: {}", eventId, userId);
        return updatedEvent;
    }

    @GetMapping("{eventId}/requests")
    public List<ParticipationResponseDto> getEventRequests(@PathVariable @Positive Long userId,
                                                           @PathVariable @Positive Long eventId) {
        log.info("Getting requests for event with ID: {} for user with ID: {}", eventId, userId);
        List<ParticipationResponseDto> requests = privateEventService.getEventRequests(userId, eventId);
        log.info("Retrieved {} requests for event with ID: {} for user with ID: {}", requests.size(), eventId, userId);
        return requests;
    }

    @PatchMapping("{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestStatus(@PathVariable @Positive Long userId,
                                                                   @PathVariable @Positive Long eventId,
                                                                   @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        log.info("Updating request status for event with ID: {} for user with ID: {}", eventId, userId);
        EventRequestStatusUpdateResult updatedStatus = privateEventService.updateEventRequestStatus(userId, eventId, request);
        log.info("Updated request status for event with ID: {} for user with ID: {}", eventId, userId);
        return updatedStatus;
    }
}
