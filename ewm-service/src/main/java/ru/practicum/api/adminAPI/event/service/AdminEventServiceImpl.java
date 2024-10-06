package ru.practicum.api.adminAPI.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.validation.ConflictException;
import ru.practicum.exception.validation.Validation;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventFullResponseDto;
import ru.practicum.model.event.dto.UpdateEventAdminRequest;
import ru.practicum.model.location.Location;
import ru.practicum.model.location.dto.CreateLocationDto;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.utils.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ObjectMapper objectMapper;
    private final Validation validation;
    private final EventMapper eventMapper;


    @Override
    @Transactional(readOnly = true)
    public List<EventFullResponseDto> getEventsByAdmin(List<Long> users, List<String> states,
                                                       List<Long> categories, LocalDateTime rangeStart,
                                                       LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("Fetching events with filters: users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);


        if (size == 0) throw new IllegalArgumentException("Size cannot be zero");
        Pageable pageable = PageRequest.of(from / size, size);

        List<Event> events = eventRepository.getAllEventsByAdmin(users, states, categories, rangeStart, rangeEnd, pageable);

        // Convert the events to DTOs
        List<EventFullResponseDto> eventsDto = events.stream()
                .map(eventMapper::toEventResponse)
                .collect(Collectors.toList());

        log.info("Fetched {} events successfully", eventsDto.size());
        return eventsDto;
    }

    @Override
    public EventFullResponseDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest adminUpdateEventDto) {
        log.info("Updating event with id: {}. New details: {}", eventId, adminUpdateEventDto);

        // Проверяем, что событие существует
        Event event = validation.checkEventExist(eventId, eventRepository);

        // Проверка, что событие можно обновлять
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) ||
                !event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Invalid event");
        }

        // Обработка изменения состояния события (публикация или отмена)
        if (adminUpdateEventDto.getStateAction() != null) {
            switch (adminUpdateEventDto.getStateAction()) {
                case PUBLISH_EVENT:
                    log.info("Publishing event with id: {}", eventId);
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    event.setViews(0L);
                    break;
                case REJECT_EVENT:
                    log.info("Rejecting event with id: {}", eventId);
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    log.warn("Unknown state action for event with id: {}", eventId);
            }
        }

        // Обновление категории события
        if (adminUpdateEventDto.getCategory() != null) {
            Category category = validation.checkCategoryExist(adminUpdateEventDto.getCategory(), categoryRepository);
            event.setEventCategory(category);
            log.info("Updated category for event with id: {}", eventId);
        }

        // Обновление местоположения события
        if (adminUpdateEventDto.getLocation() != null) {
            CreateLocationDto newLocationDto = adminUpdateEventDto.getLocation();
            Location location = event.getEventLocation();
                location.setLon(newLocationDto.getLon());
                location.setLat(newLocationDto.getLat());
            locationRepository.save(location);
            event.setEventLocation(location);
            log.info("Updated location for event with id: {}", eventId);
        }

        // Обновляем остальные поля события через ObjectMapper
        event = updateEventFields(event, adminUpdateEventDto);
        log.info("Updated event fields for event with id: {}", eventId);

        // Сохраняем обновлённое событие
        eventRepository.save(event);
        log.info("Event with id: {} saved successfully", eventId);

        // Возвращаем обновлённое событие
        return eventMapper.toEventResponse(event);
    }

    public Event updateEventFields(Event event, UpdateEventAdminRequest adminUpdateEventDto) {
        if (adminUpdateEventDto.getTitle() != null) {
            event.setTitle(adminUpdateEventDto.getTitle());
        }
        if (adminUpdateEventDto.getDescription() != null) {
            event.setDescription(adminUpdateEventDto.getDescription());
        }
        if (adminUpdateEventDto.getEventDate() != null) {
            event.setEventDate(adminUpdateEventDto.getEventDate());
        }
        if (adminUpdateEventDto.getPaid() != null) {
            event.setPaid(adminUpdateEventDto.getPaid());
        }
        if (adminUpdateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(adminUpdateEventDto.getParticipantLimit());
        }
        if (adminUpdateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(adminUpdateEventDto.getRequestModeration());
        }
        if (adminUpdateEventDto.getAnnotation() != null) {
            event.setAnnotation(adminUpdateEventDto.getAnnotation());
        }

        return event;
    }

}
