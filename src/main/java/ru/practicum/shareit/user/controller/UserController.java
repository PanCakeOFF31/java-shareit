package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody final UserRequestDto user) {
        log.debug("/users - POST: createUser({})", user);
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@RequestBody final UserRequestDto user,
                                      @PathVariable final long userId) {
        log.debug("/users/{} - PATCH: updateUser({},{})", userId, user, userId);
        return userService.updateUser(user, userId);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable final long userId) {
        log.debug("/users/{} - GET: getUserById({})", userId, userId);
        return userService.getUserResponseDtoById(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public UserResponseDto deleteUserById(@PathVariable final long userId) {
        log.debug("/users/{} - DELETE: deleteUserById({})", userId, userId);
        return userService.deleteUserById(userId);
    }

    @GetMapping
    public Collection<UserResponseDto> getUsers() {
        log.debug("/users - GET: getUsers()");
        return userService.getAll();
    }
}