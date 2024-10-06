package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.participationRequest.ParticipationRequest;
import ru.practicum.model.participationRequest.dto.ParticipationResponseDto;

@Mapper(componentModel = "spring")
public interface ParticipationMapper {

    @Mapping(source = "participationRequest.requestStatus", target = "status")
    @Mapping(source = "participationRequest.event.id", target = "event")
    @Mapping(source = "participationRequest.requester.id", target = "requester")
    ParticipationResponseDto participationRequestToResponseDto(ParticipationRequest participationRequest);
}
