package ru.practicum.api.privateAPI.event.service;

import ru.practicum.model.event.dto.CreateEventDto;
import ru.practicum.model.event.dto.EventFullResponseDto;
import ru.practicum.model.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.model.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.model.event.dto.EventShortResponseDto;
import ru.practicum.model.event.dto.UpdateEventUserRequest;
import ru.practicum.model.participationRequest.dto.ParticipationResponseDto;

import java.util.List;

public interface PrivateEventService {
    EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId,
                                                            EventRequestStatusUpdateRequest request);

    List<ParticipationResponseDto> getEventRequests(Long userId, Long eventId);

    EventFullResponseDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullResponseDto getEventById(Long userId, Long eventId);

    EventFullResponseDto addEvent(Long userId, CreateEventDto newEventDto);

    List<EventShortResponseDto> getUserEvents(Long userId, Integer from, Integer size);
}
