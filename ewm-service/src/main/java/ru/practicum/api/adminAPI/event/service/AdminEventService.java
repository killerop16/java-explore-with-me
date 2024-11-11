package ru.practicum.api.adminAPI.event.service;

import ru.practicum.model.event.dto.EventFullResponseDto;
import ru.practicum.model.event.dto.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {
    List<EventFullResponseDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                Integer from, Integer size);

    EventFullResponseDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest adminUpdateEventDto);
}
