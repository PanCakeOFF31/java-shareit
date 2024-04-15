package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.SameUserEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final String NO_FOUND_USER = "Такого пользователя с id: %d не существует в хранилище";

    @Override
    public User createUser(final User user) {
        log.info("UserServiceImpl - service.createUser({})", user);

        emailValidation(user);

        long assignedId = userRepository.addUser(user);
        user.setId(assignedId);

        return user;
    }

    @Override
    public void deleteUserById(long userId) {
        log.info("UserServiceImpl - service.deleteUserById({})", userId);

        String message = String.format(NO_FOUND_USER, userId);
        userRepository.deleteUserById(userId).orElseThrow(() -> new UserNotFoundException(message));
    }

    @Override
    public User updateUser(final long userId, final UserDto user) {
        log.info("UserServiceImpl - service.updateUser({}, {})", userId, user);

        User providedUser = UserMapper.toUser(userId, user);
        User gettedUser = getUserById(userId);

        String providedName = providedUser.getName();
        String providedEmail = providedUser.getEmail();

        if (providedName == null && providedEmail == null)
            return gettedUser;

        if (providedName != null)
            gettedUser.setName(providedName);

        if (providedEmail != null) {
            if (!providedEmail.equals(gettedUser.getEmail())) {
                emailValidation(providedUser);
            }

            gettedUser.setEmail(providedEmail);
        }

        return userRepository.updateUser(gettedUser);
    }

    @Override
    public List<User> getAll() {
        log.info("UserServiceImpl - service.getAll()");
        return userRepository.getAll();
    }

    @Override
    public User getUserById(long userId) {
        log.info("UserServiceImpl - service.getUserById({})", userId);

        String message = String.format(NO_FOUND_USER, userId);
        return userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException(message));
    }

    @Override
    public boolean containsUserById(final long userId) {
        log.info("UserServiceImpl - service.containsById()");
        return userRepository.contains(userId);
    }

    @Override
    public void userIsExist(final long userId) {
        log.info("UserServiceImpl - service.userIsExist()");

        if (!containsUserById(userId)) {
            String message = String.format(NO_FOUND_USER, userId);
            log.warn(message);
            throw new UserNotFoundException(message);
        }
    }

    private void emailValidation(final User user) {
        log.info("UserServiceImpl - service.emailValidation({})", user);

        if (userRepository.containsEmail(user.getEmail())) {
            String message = "Пользователь с таким email уже существует - " + user.getEmail();
            log.warn(message);
            throw new SameUserEmailException(message);
        }
    }
}
