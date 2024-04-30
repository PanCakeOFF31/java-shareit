package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findUserById(final long userId);

    User getUserById(final long userId);

    Optional<UserDto> findUserDtoById(final long userId);

    UserDto getUserDtoById(final long userId);

    Optional<UserBookingDto> findUserBookingDtoById(final long userId);

    UserBookingDto getUseroBokingDtoById(final long userId);

    List<UserDto> getAll();

    boolean containsUserById(final long userId);

    void userExists(final long userId);

    UserDto createUser(final UserDto userDto);

    UserDto updateUser(final UserDto userDto, final long userId);

    UserDto deleteUserById(final long userId);
}
