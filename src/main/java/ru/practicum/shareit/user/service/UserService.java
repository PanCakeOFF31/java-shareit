package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findUserById(final long userId);

    User getUserById(final long userId) throws UserNotFoundException;

    Optional<UserResponseDto> findUserResponseDtoById(final long userId);

    UserResponseDto getUserResponseDtoById(final long userId) throws UserNotFoundException;

    Optional<UserBookingDto> findUserBookingDtoById(final long userId);

    UserBookingDto getUseroBokingDtoById(final long userId) throws UserNotFoundException;

    List<UserResponseDto> getAll();

    boolean containsUserById(final long userId);

    void userExists(final long userId) throws UserNotFoundException;

    UserResponseDto createUser(final UserRequestDto userRequestDto);

    UserResponseDto updateUser(final UserRequestDto userRequestDto, final long userId);

    UserResponseDto deleteUserById(final long userId);
}
