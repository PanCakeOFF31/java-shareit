package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User getUserById(final long userId) throws UserNotFoundException;

    UserResponseDto getUserResponseDtoById(final long userId) throws UserNotFoundException;

    List<UserResponseDto> getAll(final int from, final int size);

    boolean containsUserById(final long userId);

    void userExists(final long userId) throws UserNotFoundException;

    UserResponseDto createUser(final UserRequestDto userRequestDto);

    UserResponseDto updateUser(final long userId, final UserRequestDto userRequestDto);

    UserResponseDto deleteUserById(final long userId);
}
