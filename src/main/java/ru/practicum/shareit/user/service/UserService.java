package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    boolean containsUserById(final long userId);

    void userIsExist(final long userId);

    User getUserById(final long userId);

    User createUser(final User user);

    User updateUser(final long userId, final UserDto user);

    void deleteUserById(final long userId);
}
