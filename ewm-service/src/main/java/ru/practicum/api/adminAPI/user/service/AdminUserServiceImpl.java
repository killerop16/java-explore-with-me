package ru.practicum.api.adminAPI.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.validation.Validation;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserCreateDto;
import ru.practicum.model.user.dto.UserRequestDto;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final ObjectMapper mapper;
    private final Validation validation;

    @Override
    @Transactional(readOnly = true)
    public List<UserRequestDto> findUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        log.info("Searching for users. IDs: {}, from: {}, size: {}", ids, from, size);

        List<User> users;
        if (ids == null) {
            users = userRepository.findAll(page).getContent();
            log.info("Found {} users", users.size());
        } else {
            users = userRepository.findByIdInOrderById(ids, page);
            log.info("Found {} users with specified IDs", users.size());
        }

        return users.stream()
                .map(user -> mapper.convertValue(user, UserRequestDto.class))
                .toList();
    }

    @Override
    public UserRequestDto createUser(UserCreateDto userCreateDto) {
        log.info("Creating new user with data: {}", userCreateDto);
        User user = mapper.convertValue(userCreateDto, User.class);
        user = userRepository.save(user);
        log.info("User created with ID: {}", user.getId());

        return mapper.convertValue(user, UserRequestDto.class);
    }

    @Override
    public void delUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        User user = validation.checkUserExist(userId, userRepository);
        userRepository.delete(user);
        log.info("User with ID: {} deleted", userId);
    }
}
