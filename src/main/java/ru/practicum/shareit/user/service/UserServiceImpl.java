package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.SameUserEmailException;
import ru.practicum.shareit.user.exception.UserFieldValidationException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final String NO_FOUND_USER = "Такого пользователя с id: %d не существует в хранилище";

    @Override
    public UserDto createUser(final UserDto userDto) {
        log.info("UserServiceImpl - service.createUser({})", userDto);

        final User user = UserMapper.toUser(userDto);

        emptyFieldValidation(user);
        emailValidation(user);

        long assignedId = userRepository.addUser(user);
        user.setId(assignedId);

        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUserById(long userId) {
        log.info("UserServiceImpl - service.deleteUserById({})", userId);

        String message = String.format(NO_FOUND_USER, userId);
        userRepository.deleteUserById(userId).orElseThrow(() -> new UserNotFoundException(message));
    }

    @Override
    public UserDto updateUser(final UserDto userDto, final long userId) {
        log.info("UserServiceImpl - service.updateUser({}, {})", userId, userDto);

        final User gotUser = getUserById(userId);

        final String providedName = userDto.getName();
        final String providedEmail = userDto.getEmail();

        if (providedName == null && providedEmail == null)
            return UserMapper.toUserDto(gotUser);

        if (providedName != null)
            gotUser.setName(providedName);

        if (providedEmail != null) {
            if (!providedEmail.equals(gotUser.getEmail())) {
                emailValidation(userDto);
            }

            gotUser.setEmail(providedEmail);
        }

        return UserMapper.toUserDto(userRepository.updateUser(gotUser));
    }

    @Override
    public List<UserDto> getAll() {
        log.info("UserServiceImpl - service.getAll()");
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserDtoById(long userId) {
        log.info("UserServiceImpl - service.getUserDtoById({})", userId);
        return UserMapper.toUserDto(getUserById(userId));
    }

    @Override
    public User getUserById(long userId) {
        log.info("UserServiceImpl - service.getUserById({})", userId);

        String message = String.format(NO_FOUND_USER, userId);
        return userRepository
                .findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(message));
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

    private void emailValidation(final UserDto user) {
        emailValidation(UserMapper.toUser(user));
    }

    private void emptyFieldValidation(final User user) {
        log.info("UserServiceImpl - service.emptyFieldValidation({})", user);

        String name = user.getName();
        String email = user.getEmail();

        if (name == null || email == null
                || name.isBlank() || email.isBlank()) {
            String message = "Отсутствует часть обязательны полей name/email - " + user;
            log.warn(message);
            throw new UserFieldValidationException(message);
        }
    }
}
