package ru.practicum.api.privateAPI.participationRequest.service;

import ru.practicum.model.participationRequest.dto.ParticipationResponseDto;

import java.util.List;

public interface PrivateParticipationRequestService {
    List<ParticipationResponseDto> findParticipationRequests(Long userId);

    ParticipationResponseDto createParticipationRequest(Long userId, Long eventId);

    ParticipationResponseDto updateParticipationRequest(Long userId, Long requestId);
}
