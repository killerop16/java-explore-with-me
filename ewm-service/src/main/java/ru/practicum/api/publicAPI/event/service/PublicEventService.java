package ru.practicum.api.publicAPI.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.model.event.dto.EventFullResponseDto;
import ru.practicum.model.event.dto.EventShortResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {
    EventFullResponseDto getEventById(Long id, HttpServletRequest request);

    List<EventShortResponseDto> getEvents(String text, List<Long> categories, Boolean paid,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                          Integer from, Integer size, String sort, HttpServletRequest request);
}
