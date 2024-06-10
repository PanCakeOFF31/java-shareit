package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody final UserRequestDto user) {
        log.debug("/users - POST: createUser({})", user);
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@PathVariable final long userId,
                                      @RequestBody final UserRequestDto user) {
        log.debug("/users/{} - PATCH: updateUser({},{})", userId, user, userId);
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public UserResponseDto deleteUserById(@PathVariable final long userId) {
        log.debug("/users/{} - DELETE: deleteUserById({})", userId, userId);
        return userService.deleteUserById(userId);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable final long userId) {
        log.debug("/users/{} - GET: getUserById({})", userId, userId);
        return userService.getUserResponseDtoById(userId);
    }

    @GetMapping
    public List<UserResponseDto> getUsers(@RequestParam final int from,
                                          @RequestParam final int size) {
        log.debug("/users - GET: getUsers()");
        return userService.getAll(from, size);
    }
}