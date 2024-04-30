package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
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
    public UserDto createUser(@Valid @RequestBody final UserDto user) {
        log.debug("/users - POST: createUser({})", user);
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Valid @RequestBody final UserDto user,
                              @PathVariable final long userId) {
        log.debug("/users/{} - PATCH: updateUser({},{})", userId, user, userId);
        return userService.updateUser(user, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable final long userId) {
        log.debug("/users/{} - GET: getUserById({})", userId, userId);
        return userService.getUserDtoById(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public UserDto deleteUserById(@PathVariable final long userId) {
        log.debug("/users/{} - DELETE: deleteUserById({})", userId, userId);
        return userService.deleteUserById(userId);
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        log.debug("/users - GET: getUsers()");
        return userService.getAll();
    }
}