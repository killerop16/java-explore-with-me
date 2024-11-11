package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.model.compilation.dto.CreateCompilationDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventShortResponseDto;



@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(CreateCompilationDto createCompilationDto);

    @Mapping(target = "category", source = "event.eventCategory")
    EventShortResponseDto toEventShortDto(Event event);

    CompilationResponseDto toResponseDto(Compilation compilation);

}

