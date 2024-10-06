package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryResponseDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventFullResponseDto;
import ru.practicum.model.event.dto.EventShortResponseDto;
import ru.practicum.model.location.Location;
import ru.practicum.model.location.dto.CreateLocationDto;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "category", source = "event.eventCategory")
    EventShortResponseDto toEventShortDto(Event event);

    @Mapping(target = "category", source = "event.eventCategory")
    @Mapping(target = "location", source = "event.eventLocation")
    EventFullResponseDto toEventResponse(Event event);

    // Маппинг для категории и локации
    CategoryResponseDto toCategoryResponseDto(Category category);

    CreateLocationDto toLocationDto(Location location);
}
