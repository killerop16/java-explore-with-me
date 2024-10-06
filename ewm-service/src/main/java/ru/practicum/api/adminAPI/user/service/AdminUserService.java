package ru.practicum.api.adminAPI.user.service;

import ru.practicum.model.user.dto.UserCreateDto;
import ru.practicum.model.user.dto.UserRequestDto;

import java.util.List;

public interface AdminUserService {

    List<UserRequestDto> findUsers(List<Long> ids, Integer from, Integer size);

    UserRequestDto createUser(UserCreateDto userCreateDto);

    void delUser(Long userId);
}
