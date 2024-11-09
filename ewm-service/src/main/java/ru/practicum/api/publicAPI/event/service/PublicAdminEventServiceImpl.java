package ru.practicum.api.publicAPI.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.exception.validation.DateException;
import ru.practicum.exception.validation.ResourceNotFoundException;
import ru.practicum.repository.RepositoryHelper;
import ru.practicum.hit.HitRequest;
import ru.practicum.hit.HitResponse;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventFullResponseDto;
import ru.practicum.model.event.dto.EventShortResponseDto;
import ru.practicum.repository.EventRepository;
import ru.practicum.utils.Constants;
import ru.practicum.utils.EventState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicAdminEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final ObjectMapper mapper;
    private final RepositoryHelper validation;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(readOnly = true)
    public EventFullResponseDto getEventById(Long id, HttpServletRequest request) {
        Event event = validation.getEventIfExist(id, eventRepository);

        if (event.getState() == null || !event.getState().equals(EventState.PUBLISHED)) {
            throw new ResourceNotFoundException(String.format("Событие с ID %d недоступно", id));
        }

        // Отправка данных о запросе для статистики
        statsClient.sendHit(createHitRequest(request));

        // Получение уникальных просмотров события
        List<HitResponse> hitResponses = statsClient.getStats(Constants.DATE_TIME_START,
                LocalDateTime.now().format(formatter),
                        List.of("/events/" + id), true)
                .block(); // ожидаем получения результата

        // Извлечение количества просмотров
        Integer views = hitResponses.stream()
                .filter(hit -> hit.getUri().equals("/events/" + id))
                .map(HitResponse::getHits)
                .findFirst()
                .orElse(0); // если не найдено, устанавливаем 0

        // Заполнение DTO данными из события
        EventFullResponseDto eventFullResponseDto = eventMapper.toEventResponse(event);
        eventFullResponseDto.setViews(views.longValue());

        return eventFullResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortResponseDto> getEvents(String text, List<Long> categories, Boolean paid,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                 Boolean onlyAvailable, Integer from, Integer size,
                                                 String sort, HttpServletRequest request) {

        validDates(rangeStart, rangeEnd);

        // Отправляем статистику о запросе
        statsClient.sendHit(createHitRequest(request));

        // Устанавливаем диапазон дат
        LocalDateTime start = (rangeStart != null) ? rangeStart : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? rangeEnd : LocalDateTime.now().plusYears(1);
        Pageable pageable = PageRequest.of(from / size, size);

        log.debug("Text: {}, Categories: {}, Paid: {}, RangeStart: {}, RangeEnd: {}", text, categories, paid, start, end);
        List<Event> events = eventRepository.getPublicEvents(text, categories, paid, start, end, pageable);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        // Фильтрация доступных событий (если запрашивается)
        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() > event.getConfirmedRequests())
                    .toList();
        }

        // Сортировка по нужному критерию
        events = sortEvents(events, sort);

        return events.stream()
                .map(event -> {
                    EventShortResponseDto dto = eventMapper.toEventShortDto(event);
                    dto.setViews(getEventViews(event.getId())); // Загрузка просмотров
                    return dto;
                })
                .toList();
    }

    private HitRequest createHitRequest(HttpServletRequest request) {
        HitRequest hitRequest = new HitRequest();
        hitRequest.setUri(request.getRequestURI());
        hitRequest.setIp(request.getRemoteAddr());
        return hitRequest;
    }

    // Метод для сортировки событий
    private List<Event> sortEvents(List<Event> events, String sort) {
        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE" -> events.sort(Comparator.comparing(Event::getEventDate));
                case "VIEWS" -> events.sort(Comparator.comparing(Event::getViews).reversed());
                default -> throw new IllegalArgumentException("Invalid sort parameter");
            }
        }
        return events;
    }

    // Метод для получения количества просмотров события
    private Long getEventViews(Long eventId) {
        List<HitResponse> hitResponses = statsClient.getStats(Constants.DATE_TIME_START,
                        LocalDateTime.now().format(formatter),
                        List.of("/events/" + eventId), true)
                .block();
        return hitResponses.stream()
                .filter(hit -> hit.getUri().equals("/events/" + eventId))
                .map(HitResponse::getHits)
                .findFirst()
                .orElse(0)
                .longValue();
    }

    // Метод для валидации дат
    private void validDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new DateException("Конечная дата не может быть раньше начальной");
        }
    }
}
