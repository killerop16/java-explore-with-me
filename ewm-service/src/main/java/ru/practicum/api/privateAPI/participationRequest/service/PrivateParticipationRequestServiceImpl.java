package ru.practicum.api.privateAPI.participationRequest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.validation.ConflictException;
import ru.practicum.exception.validation.ResourceNotFoundException;
import ru.practicum.repository.RepositoryHelper;
import ru.practicum.mapper.ParticipationMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.participationRequest.ParticipationRequest;
import ru.practicum.model.participationRequest.dto.ParticipationResponseDto;
import ru.practicum.model.user.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.utils.EventState;
import ru.practicum.utils.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateParticipationRequestServiceImpl implements PrivateParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final RepositoryHelper validation;
    private final ParticipationMapper participationMapper;

    @Override
    public List<ParticipationResponseDto> findParticipationRequests(Long userId) {
        log.info("Finding participation requests for user with ID: {}", userId);
        validation.getUserIfExist(userId, userRepository); // Проверка на валидность пользователя

        List<ParticipationRequest> requests = participationRequestRepository.findByRequesterId(userId);

        List<ParticipationResponseDto> responseDtos = requests.stream()
                .map(participationMapper::participationRequestToResponseDto)
                .collect(Collectors.toList());

        log.info("Found {} participation requests for user with ID: {}", responseDtos.size(), userId);
        return responseDtos;
    }

    @Override
    public ParticipationResponseDto createParticipationRequest(Long userId, Long eventId) {
        log.info("Creating participation request for user ID: {} for event ID: {}", userId, eventId);
        User user = validation.getUserIfExist(userId, userRepository);

        Event event = validation.getEventIfExist(eventId, eventRepository);

        ParticipationRequest old = participationRequestRepository.findByEventIdAndRequesterId(eventId, userId);

        if (old != null) {
            throw new IllegalArgumentException("!");
        }

        // Проверка, является ли инициатор события
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event initiator cannot participate in their own event");
        }

        // Проверка на состояние события
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Cannot participate in unpublished event");
        }

        // Проверка лимита участников
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException("Event participant limit reached");
        }

        // Создание запроса на участие
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setEvent(event);
        participationRequest.setRequester(user);
        participationRequest.setCreated(LocalDateTime.now());

        // Проверка на существование повторного запроса
        if (participationRequestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Duplicate participation request");
        }

        if (event.getParticipantLimit() == 0) {
            participationRequest.setRequestStatus(RequestStatus.CONFIRMED);
        } else {
            participationRequest.setRequestStatus(RequestStatus.PENDING);
        }

        if (!event.getRequestModeration() && event.getParticipantLimit() != 0) {
            participationRequest.setRequestStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        // Сохранение запроса на участие
        participationRequest = participationRequestRepository.save(participationRequest);

        // Преобразование запроса в DTO
        ParticipationResponseDto responseDto = participationMapper.participationRequestToResponseDto(participationRequest);

        log.info("Participation request created successfully for user ID: {} with request ID: {}", userId, responseDto.getId());
        return responseDto;
    }

    @Override
    @Transactional
    public ParticipationResponseDto updateParticipationRequest(Long userId, Long requestId) {
        ParticipationRequest request = validation.getParticipationRequestIfExist(requestId, participationRequestRepository);

        if (!request.getRequester().getId().equals(userId)) {
            throw new ResourceNotFoundException("User with id=" + userId + " does not have permission to cancel this request");
        }

        // Проверка, может ли запрос быть отменен (например, статус должен быть PENDING)
        if (!request.getRequestStatus().equals(RequestStatus.PENDING)) {
            throw new ConflictException("Request with id=" + requestId + " cannot be canceled because it is not in a cancellable state");
        }

        // Отмена запроса
        request.setRequestStatus(RequestStatus.CANCELED);
        ParticipationRequest updatedRequest = participationRequestRepository.save(request);

        // Преобразование сущности запроса в DTO
        return participationMapper.participationRequestToResponseDto(updatedRequest);
    }
}
