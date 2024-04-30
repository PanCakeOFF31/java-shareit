package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.SameUserEmailException;
import ru.practicum.shareit.user.exception.UserFieldValidationException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final String NO_FOUND_USER = "Такого пользователя с id: %d не существует в хранилище";

    @Override
    public Optional<User> findUserById(final long userId) {
        log.info("UserServiceImpl - service.findUserById({})", userId);
        return userRepository.findById(userId);
    }

    @Override
    public User getUserById(final long userId) {
        log.info("UserServiceImpl - service.getUserById({})", userId);
        String message = String.format(NO_FOUND_USER, userId);
        return findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(message));
    }

    @Override
    public Optional<UserDto> findUserDtoById(final long userId) {
        log.info("UserServiceImpl - service.findUserDtoById({})", userId);
        return userRepository.findUserDtoById(userId);
    }

    @Override
    public UserDto getUserDtoById(long userId) {
        log.info("UserServiceImpl - service.getUserDtoById({})", userId);
        String message = String.format(NO_FOUND_USER, userId);
        return findUserDtoById(userId)
                .orElseThrow(() -> new UserNotFoundException(message));
    }

    @Override
    public Optional<UserBookingDto> findUserBookingDtoById(final long userId) {
        log.info("UserServiceImpl - service.findUserBookingDto({})", userId);
        return userRepository.findUserBookingDtoById(userId);
    }

    @Override
    public UserBookingDto getUseroBokingDtoById(final long userId) {
        log.info("UserServiceImpl - service.getUserBookingDtoById({})", userId);
        String message = String.format(NO_FOUND_USER, userId);
        return findUserBookingDtoById(userId)
                .orElseThrow(() -> new UserNotFoundException(message));
    }

    @Override
    public boolean containsUserById(final long userId) {
        log.info("UserServiceImpl - service.containsById()");
        return userRepository.existsById(userId);
    }

    @Override
    public void userExists(final long userId) {
        log.info("UserServiceImpl - service.userIsExist()");

        if (!containsUserById(userId)) {
            String message = String.format(NO_FOUND_USER, userId);
            log.warn(message);
            throw new UserNotFoundException(message);
        }
    }

    @Override
    public UserDto createUser(final UserDto userDto) {
        log.info("UserServiceImpl - service.createUser({})", userDto);

        final User user = UserMapper.mapToUser(userDto);

        emptyFieldValidation(user);

        final User savedUser = userRepository.save(user);

        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto deleteUserById(long userId) {
        log.info("UserServiceImpl - service.deleteUserById({})", userId);

        var deletedUser = getUserDtoById(userId);
        userRepository.deleteById(userId);

        return deletedUser;
    }

    @Override
    public UserDto updateUser(final UserDto userDto, final long userId) {
        log.info("UserServiceImpl - service.updateUser({}, {})", userId, userDto);

        final User gotUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(NO_FOUND_USER, userId)));

        final String providedName = userDto.getName();
        final String providedEmail = userDto.getEmail();

        if (providedName == null && providedEmail == null) {
            log.info("Прислан пользователь User без обновляемых полей. Никакого обновления не произошло");
            return UserMapper.mapToUserDto(gotUser);
        }

        if (providedName != null)
            gotUser.setName(providedName);

        if (providedEmail != null) {
            if (!providedEmail.equals(gotUser.getEmail())) {
                emailValidation(userDto.getEmail());
            }

            gotUser.setEmail(providedEmail);
        }

        return UserMapper.mapToUserDto(userRepository.save(gotUser));
    }

    @Override
    public List<UserDto> getAll() {
        log.debug("UserServiceImpl - service.getAll()");

        return userRepository.findAll(Pageable.ofSize(50)).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    private void emailValidation(final String email) {
        log.info("UserServiceImpl - service.emailValidation({})", email);

        if (userRepository.findUserByEmail(email).isPresent()) {
            String message = "Пользователь с таким email уже существует - " + email;
            log.warn(message);
            throw new SameUserEmailException(message);
        }
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
