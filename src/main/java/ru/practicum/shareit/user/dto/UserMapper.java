package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(final User user) {
        return UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(final long userId, final UserDto userDto) {
        return User.builder()
                .id(userId)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
