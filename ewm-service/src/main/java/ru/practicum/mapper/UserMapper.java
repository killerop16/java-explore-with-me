package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserShortDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserShortDto toUserShortDto(User user);
}
