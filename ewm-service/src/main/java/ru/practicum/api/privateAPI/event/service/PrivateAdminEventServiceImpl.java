package ru.practicum.api.privateAPI.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.validation.ConflictException;
import ru.practicum.repository.RepositoryHelper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.ParticipationMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.CreateEventDto;
import ru.practicum.model.event.dto.EventFullResponseDto;
import ru.practicum.model.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.model.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.model.event.dto.EventShortResponseDto;
import ru.practicum.model.event.dto.UpdateEventUserRequest;
import ru.practicum.model.location.Location;
import ru.practicum.model.location.dto.CreateLocationDto;
import ru.practicum.model.participationRequest.ParticipationRequest;
import ru.practicum.model.participationRequest.dto.ParticipationResponseDto;
import ru.practicum.model.user.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.utils.EventState;
import ru.practicum.utils.RequestStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateAdminEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRepository;
    private final ObjectMapper objectMapper;
    private final ParticipationMapper participationMapper;
    private final EventMapper eventMapper;
    private final RepositoryHelper validation;

    @Override

    public EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest request) {
        log.info("Updating participation request statuses for event {} by user {}", eventId, userId);
        validation.getUserIfExist(userId, userRepository);
        Event event = validation.getEventIfExist(eventId, eventRepository);

        List<ParticipationRequest> participationRequests = participationRepository.findByEventIdAndIdIn(eventId, request.getRequestIds());

        if (participationRequests.isEmpty()) {
            throw new IllegalArgumentException("No participation requests found for the given event and request IDs");
        }

        // Обновляем статусы заявок
        List<ParticipationResponseDto> confirmedRequests = new ArrayList<>();
        List<ParticipationResponseDto> rejectedRequests = new ArrayList<>();

        for (ParticipationRequest participationRequest : participationRequests) {
            if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
                throw new ConflictException("Invalid event");
            }

            if (!participationRequest.getRequestStatus().equals(RequestStatus.PENDING)) {
                throw new IllegalStateException("Only pending requests can be updated");
            }

            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                // Шаг 4: Проверяем лимит участников
                if ((event.getConfirmedRequests() != null && event.getParticipantLimit() != null) &&
                        event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    participationRequest.setRequestStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(participationMapper.participationRequestToResponseDto(participationRequest));
                } else {
                    participationRequest.setRequestStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(participationMapper.participationRequestToResponseDto(participationRequest));

                    if (event.getConfirmedRequests() == null) {
                        event.setConfirmedRequests(0);
                    }
                    // Обновляем количество подтвержденных заявок
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                }
            } else if (request.getStatus().equals(RequestStatus.REJECTED)) {
                participationRequest.setRequestStatus(RequestStatus.REJECTED);
                rejectedRequests.add(participationMapper.participationRequestToResponseDto(participationRequest));
            }
        }

        // Сохраняем обновленные заявки
        participationRepository.saveAll(participationRequests);
        // Обновляем событие
        eventRepository.save(event);

        // Возвращаем результат
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);

        log.info("Event requests status updated for event id {}", eventId);
        return result;
    }


    @Override
    @Transactional(readOnly = true)
    public List<ParticipationResponseDto> getEventRequests(Long userId, Long eventId) {
        log.info("Getting participation requests for event {} by user {}", eventId, userId);

        validation.getUserIfExist(userId, userRepository);
        validation.getEventIfExist(eventId, eventRepository);

        // Получение запросов на участие
        List<ParticipationRequest> requests = participationRepository.findByEventId(eventId);

        return requests.stream()
                .map(participationMapper::participationRequestToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullResponseDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Updating event id {} for user {}", eventId, userId);

        // Проверка события и пользователя
        Event event = validation.getEventIfExist(eventId, eventRepository);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2)) ||
                event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalArgumentException("Invalid event");
        }

        // Обновление полей события на основе UpdateEventUserRequest
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            // Проверка даты события (например, оно должно быть в будущем)
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getCategory() != null) {
            // Проверка категории
            Category category = validation.getCategoryIfExist(updateEventUserRequest.getCategory(), categoryRepository);
            event.setEventCategory(category);
        }
        if (updateEventUserRequest.getLocation() != null) {
            // Проверка и обновление местоположения
            Location convertValue = objectMapper.convertValue(updateEventUserRequest.getLocation(), Location.class);
            Location location = validation.getLocationIfExist(convertValue.getId(), locationRepository);
            event.setEventLocation(location);
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        // Обновление статуса события
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(EventState.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (updateEventUserRequest.getStateAction().equals(EventState.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            } else throw new IllegalArgumentException("Invalid event");
        }

        // Сохранение обновленного события в базе данных
        eventRepository.save(event);

        log.info("Event id {} updated successfully for user {}", eventId, userId);

        // Возвращение обновленного события в виде DTO
        return eventMapper.toEventResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullResponseDto getEventById(Long userId, Long eventId) {
        log.info("Getting event by id {} for user {}", eventId, userId);

        validation.getUserIfExist(userId, userRepository);

        Event event = validation.getEventIfExist(eventId, eventRepository);
        return eventMapper.toEventResponse(event);
    }

    @Override
    @Transactional
    public EventFullResponseDto addEvent(Long userId, CreateEventDto newEventDto) {
        log.info("Creating new event for user {}", userId);

        // Проверка пользователя
        User user = validation.getUserIfExist(userId, userRepository);
        // Проверка категории
        Category category = validation.getCategoryIfExist(newEventDto.getCategory(), categoryRepository);

        // Проверка локации
        CreateLocationDto locationDto = newEventDto.getLocation();
        Location location = locationRepository.findFirstByLatAndLon(locationDto.getLat(), locationDto.getLon());
        if (location == null) {
            location = locationRepository.save(objectMapper.convertValue(locationDto, Location.class));
        }

        // Создание нового события
        Event event = objectMapper.convertValue(newEventDto, Event.class);
        event.setInitiator(user);
        event.setEventCategory(category);
        event.setEventLocation(location);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setConfirmedRequests(0);
        // Сохранение события в базе
        Event savedEvent = eventRepository.save(event);

        log.info("Event created successfully with id {}", savedEvent.getId());

        return eventMapper.toEventResponse(savedEvent);
    }


    @Override
    @Transactional(readOnly = true)
    public List<EventShortResponseDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.info("Retrieving events for user {}", userId);

        validation.getUserIfExist(userId, userRepository);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);


        log.info("Found {} events for user {}", events.size(), userId);

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }
}
