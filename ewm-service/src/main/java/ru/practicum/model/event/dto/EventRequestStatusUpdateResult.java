package ru.practicum.model.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.participationRequest.dto.ParticipationResponseDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResult {
    private List<ParticipationResponseDto> confirmedRequests;
    private List<ParticipationResponseDto> rejectedRequests;
}
