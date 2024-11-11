package ru.practicum.api.adminAPI.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.adminAPI.user.service.AdminUserService;
import ru.practicum.model.user.dto.UserCreateDto;
import ru.practicum.model.user.dto.UserRequestDto;
import ru.practicum.utils.Constants;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Slf4j
@Validated
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    public List<UserRequestDto> findUsers(@RequestParam(required = false) List<Long> ids,
                                          @RequestParam(required = false, defaultValue = Constants.ZERO)
                                          @PositiveOrZero Integer from,
                                          @RequestParam(required = false, defaultValue = Constants.TEN)
                                          @Positive Integer size) {
        log.info("Received request to find users. IDs: {}, from: {}, size: {}", ids, from, size);
        List<UserRequestDto> users = adminUserService.findUsers(ids, from, size);
        log.info("Returning {} users", users.size());
        return users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserRequestDto createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        log.info("Received request to create a new user with data: {}", userCreateDto);
        UserRequestDto createdUser = adminUserService.createUser(userCreateDto);
        log.info("User created with ID: {}", createdUser.getId());
        return createdUser;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delUserById(@PathVariable @Positive Long userId) {
        log.info("Received request to delete user with ID: {}", userId);
        adminUserService.delUser(userId);
        log.info("User with ID: {} successfully deleted", userId);
    }
}
