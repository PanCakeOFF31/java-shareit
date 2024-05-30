package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody final UserRequestDto user) {
        log.debug("/users - POST: createUser({})", user);
        return userClient.createUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable final long userId,
                                             @RequestBody final UserRequestDto user) {
        log.debug("/users/{} - PATCH: updateUser({},{})", userId, user, userId);
        return userClient.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable final long userId) {
        log.debug("/users/{} - DELETE: deleteUserById({})", userId, userId);
        return userClient.deleteUserById(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable final long userId) {
        log.debug("/users/{} - GET: getUserById({})", userId, userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers(@RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                           @RequestParam(defaultValue = "10") @Positive final int size) {
        log.debug("/users - GET: getUsers()");
        return userClient.getUsers(from, size);
    }
}
