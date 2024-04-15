package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody final User user) {
        log.info("/users - POST: createUser({})", user);
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@Valid @RequestBody final UserDto user,
                           @PathVariable final long userId) {
        log.info("/users/{} - PATCH: updateUser({},{})", userId, user, userId);
        return userService.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable final long userId) {
        log.info("/users/{} - GET: getUserById({})", userId, userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable final long userId) {
        log.info("/users/{} - DELETE: deleteUserById({})", userId, userId);
        userService.deleteUserById(userId);
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("/users - GET: getUsers()");
        return userService.getAll();
    }
}