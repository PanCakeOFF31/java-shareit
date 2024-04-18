package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    boolean containsUserById(final long userId);

    void userIsExist(final long userId);

    UserDto getUserDtoById(final long userId);

    User getUserById(final long userId);

    UserDto createUser(final UserDto userDto);

    UserDto updateUser(final UserDto userDto, final long userId);

    void deleteUserById(final long userId);
}
