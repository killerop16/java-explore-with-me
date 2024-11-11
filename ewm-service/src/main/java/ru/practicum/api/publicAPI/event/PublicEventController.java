package ru.practicum.api.publicAPI.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.publicAPI.event.service.PublicEventService;
import ru.practicum.model.event.dto.EventFullResponseDto;
import ru.practicum.model.event.dto.EventShortResponseDto;
import ru.practicum.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.utils.Constants.EVENT_DATE;
import static ru.practicum.utils.Constants.FALSE;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicEventController {
    private final PublicEventService publicEventService;

    @GetMapping
    public List<EventShortResponseDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeStart,
            @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = FALSE) Boolean onlyAvailable,
            @RequestParam(defaultValue = Constants.ZERO) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = Constants.TEN) @Positive Integer size,
            @RequestParam(required = false, defaultValue = EVENT_DATE) String sort,
            HttpServletRequest request) {
        log.info("Received request to get events with filters - text: {}, categories: {}, paid: {}, rangeStart: {}," +
                        " rangeEnd: {}, onlyAvailable: {}, from: {}, size: {}, sort: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size, sort);

        List<EventShortResponseDto> events = publicEventService.getEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, from, size, sort, request);
        log.info("Found {} events", events.size());

        return events;
    }


    @GetMapping("/{id}")
    public EventFullResponseDto getEventById(@PathVariable @Positive Long id,
                                             HttpServletRequest request) {
        log.info("Received request to get event with id: {}", id);

        EventFullResponseDto event = publicEventService.getEventById(id, request);

        log.info("Event with id: {} retrieved successfully", id);
        return event;
    }
}
