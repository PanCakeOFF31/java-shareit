package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    long addUser(final User user);

    User updateUser(final User user);

    Optional<User> findUserById(final long userId);

    boolean contains(final long userId);

    Optional<User> deleteUserById(final long userId);

    boolean containsEmail(final String email);

    List<User> getAll();
}
