package ru.practicum.api.adminAPI.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.adminAPI.event.service.AdminEventService;
import ru.practicum.model.event.dto.EventFullResponseDto;
import ru.practicum.model.event.dto.UpdateEventAdminRequest;
import ru.practicum.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminEventController {
    private final AdminEventService adminEventService;

    @GetMapping
    public List<EventFullResponseDto> getEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) List<String> states,
                                                @RequestParam(required = false) List<Long> categories,
                                                @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
                                        @RequestParam(required = false) LocalDateTime rangeStart,
                                                @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
                                        @RequestParam(required = false) LocalDateTime rangeEnd,
                                                @PositiveOrZero
                                        @RequestParam(defaultValue = Constants.ZERO) Integer from,
                                                @Positive
                                        @RequestParam(defaultValue = Constants.TEN) Integer size) {

        log.info("Fetching events with filters: users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        List<EventFullResponseDto> events = adminEventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Fetched {} events", events.size());
        return events;
    }

    @PatchMapping("/{eventId}")
    public EventFullResponseDto updateEventByAdmin(@PathVariable @Positive Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest adminUpdateEventDto) {
        log.info("Updating event with id: {} with data: {}", eventId, adminUpdateEventDto);
        EventFullResponseDto updatedEvent = adminEventService.updateEventByAdmin(eventId, adminUpdateEventDto);
        log.info("Updated event: {}", updatedEvent);
        return updatedEvent;
    }
}
