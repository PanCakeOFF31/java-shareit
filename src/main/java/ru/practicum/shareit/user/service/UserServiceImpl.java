package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exception.EmailFieldValidationException;
import ru.practicum.shareit.user.exception.SameUserEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final String NO_FOUND_USER = "Такого пользователя с id: %d не существует в хранилище";

    private Optional<User> findUserById(final long userId) {
        log.info("UserServiceImpl - service.findUserById({})", userId);
        return userRepository.findById(userId);
    }

    @Override
    public User getUserById(final long userId) throws UserNotFoundException {
        log.info("UserServiceImpl - service.getUserById({})", userId);
        String message = String.format(NO_FOUND_USER, userId);
        return findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(message));
    }

    private Optional<UserResponseDto> findUserResponseDtoById(final long userId) {
        log.info("UserServiceImpl - service.findUserResponseDtoById({})", userId);
        return userRepository.findUserResponseDtoById(userId);
    }

    @Override
    public UserResponseDto getUserResponseDtoById(long userId) throws UserNotFoundException {
        log.info("UserServiceImpl - service.getUserResponseDtoById({})", userId);
        String message = String.format(NO_FOUND_USER, userId);
        return this.findUserResponseDtoById(userId)
                .orElseThrow(() -> new UserNotFoundException(message));
    }

    @Override
    public boolean containsUserById(final long userId) {
        log.info("UserServiceImpl - service.containsUserById()");
        return userRepository.existsById(userId);
    }

    @Override
    public void userExists(final long userId) throws UserNotFoundException {
        log.info("UserServiceImpl - service.userExists()");

        if (!containsUserById(userId)) {
            String message = String.format(NO_FOUND_USER, userId);
            log.warn(message);
            throw new UserNotFoundException(message);
        }
    }

    @Transactional
    @Override
    public UserResponseDto createUser(final UserRequestDto userRequestDto) {
        log.info("UserServiceImpl - service.createUser({})", userRequestDto);

        final User user = UserMapper.mapToUser(userRequestDto);

        return UserMapper.mapToUserResponseDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponseDto deleteUserById(long userId) {
        log.info("UserServiceImpl - service.deleteUserById({})", userId);

        var deletedUser = getUserResponseDtoById(userId);
        userRepository.deleteById(userId);

        return deletedUser;
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(final UserRequestDto userRequestDto, final long userId) {
        log.info("UserServiceImpl - service.updateUser({}, {})", userId, userRequestDto);

        final User gotUser = getUserById(userId);

        final String providedName = userRequestDto.getName();
        final String providedEmail = userRequestDto.getEmail();

        if (providedName == null && providedEmail == null) {
            log.info("Прислан пользователь User без обновляемых полей. Никакого обновления не произошло");
            return UserMapper.mapToUserResponseDto(gotUser);
        }

        if (providedName != null)
            gotUser.setName(providedName);

        if (providedEmail != null) {
            if (!providedEmail.equals(gotUser.getEmail())) {
                emailValidation(userRequestDto.getEmail());
            }

            gotUser.setEmail(providedEmail);
        }

        return UserMapper.mapToUserResponseDto(userRepository.save(gotUser));
    }

    @Override
    public List<UserResponseDto> getAll() {
        log.debug("UserServiceImpl - service.getAll()");

        return userRepository.findAll(Pageable.ofSize(100)).stream()
                .map(UserMapper::mapToUserResponseDto)
                .collect(Collectors.toList());
    }

    private void emailValidation(final String email) {
        log.info("UserServiceImpl - service.emailValidation({})", email);

        if (!email.matches("^[\\w-\\.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$")) {
            String message = "Нарушение валидации email - " + email;
            log.warn(message);
            throw new EmailFieldValidationException(message);
        }

        if (userRepository.findUserByEmail(email).isPresent()) {
            String message = "Пользователь с таким email уже существует - " + email;
            log.warn(message);
            throw new SameUserEmailException(message);
        }
    }
}
