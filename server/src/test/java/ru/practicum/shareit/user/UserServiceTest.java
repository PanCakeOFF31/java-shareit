package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exception.EmailFieldValidationException;
import ru.practicum.shareit.user.exception.SameUserEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private long anyUserId;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;
    private User expectedUser;

    @BeforeEach
    public void preTestInitialization() {
        anyUserId = 123L;

        userRequestDto = UserRequestDto.builder()
                .name("maxim")
                .email("mak@yandex.ru")
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(anyUserId)
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .build();

        expectedUser = User.builder()
                .id(anyUserId)
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .build();
    }

    @Test
    public void test_T0010_PS01_createUser() {
        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(expectedUser);

        UserResponseDto createdUser = userService.createUser(userRequestDto);
        assertEquals(userResponseDto, createdUser);

        Mockito.verify(userRepository, Mockito.only()).save(any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void test_T0020_PS01_updateUser() {
        Mockito.when(userRepository.findById(anyUserId))
                .thenReturn(Optional.of(expectedUser));

        Mockito.when(userRepository.save(expectedUser))
                .thenReturn(expectedUser);

        UserResponseDto updated = userService.updateUser(anyUserId, userRequestDto);
        assertEquals(userResponseDto, updated);

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyUserId);
        Mockito.verify(userRepository, Mockito.times(0)).findUserByEmail(expectedUser.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(expectedUser);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void test_T0020_PS02_updateUser_otherEmail() {
        String newEmail = "mail@mail.ru";

        UserRequestDto userRequestDto1 = userRequestDto.toBuilder().email(newEmail).build();
        UserResponseDto userResponseDto1 = userResponseDto.toBuilder().email(newEmail).build();
        User updatedUser = expectedUser.toBuilder().email(newEmail).build();

        Mockito.when(userRepository.findById(anyUserId))
                .thenReturn(Optional.of(expectedUser.toBuilder().build()));

        Mockito.when(userRepository.findUserByEmail(newEmail))
                .thenReturn(Optional.empty());

        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);

        UserResponseDto updated = userService.updateUser(anyUserId, userRequestDto1);
        assertEquals(userResponseDto1, updated);

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyUserId);
        Mockito.verify(userRepository, Mockito.times(1)).findUserByEmail(userRequestDto1.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void test_T0020_PS03_updateUser_withEmptyFields() {
        Mockito.when(userRepository.findById(anyUserId))
                .thenReturn(Optional.of(expectedUser));

        UserResponseDto updated = userService.updateUser(anyUserId, new UserRequestDto());
        assertEquals(userResponseDto, updated);

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyUserId);
        Mockito.verify(userRepository, Mockito.never()).findUserByEmail(anyString());
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    public void test_T0030_NS01_updateUser_noUserWithId() {
        Mockito.when(userRepository.findById(anyUserId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(anyUserId, new UserRequestDto()));

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyUserId);
        Mockito.verify(userRepository, Mockito.never()).findUserByEmail(anyString());
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }


    @Test
    public void test_T0030_NS02_updateUser_emailValidation() {
        String invalidEmail = "invalid.mail.ru";

        Mockito.when(userRepository.findById(anyUserId))
                .thenReturn(Optional.of(expectedUser));

        assertThrows(EmailFieldValidationException.class, () -> userService.updateUser(anyUserId, new UserRequestDto("any name", invalidEmail)));

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyUserId);
        Mockito.verify(userRepository, Mockito.never()).findUserByEmail(invalidEmail);
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void test_T0030_NS03_updateUser_sameEmailValidation() {
        String invalidEmail = "invalid@mail.ru";

        Mockito.when(userRepository.findById(anyUserId))
                .thenReturn(Optional.of(expectedUser));

        Mockito.when(userRepository.findUserByEmail(invalidEmail))
                .thenReturn(Optional.of(new User()));

        assertThrows(SameUserEmailException.class, () -> userService.updateUser(anyUserId, new UserRequestDto("any name", invalidEmail)));

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyUserId);
        Mockito.verify(userRepository, Mockito.times(1)).findUserByEmail(invalidEmail);
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }


    @Test
    public void test_T0040_PS01_getUserById() {
        Mockito.when(userRepository.findUserResponseDtoById(anyUserId))
                .thenReturn(Optional.of(userResponseDto));

        UserResponseDto gotUser = userService.getUserResponseDtoById(anyUserId);
        assertEquals(gotUser, userResponseDto);

        Mockito.verify(userRepository, Mockito.only()).findUserResponseDtoById(anyUserId);
        Mockito.verify(userRepository, Mockito.times(1)).findUserResponseDtoById(anyUserId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void test_T0040_NS01_getUserById_noUserWithId() {
        Mockito.when(userRepository.findUserResponseDtoById(anyUserId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserResponseDtoById(anyUserId));

        Mockito.verify(userRepository, Mockito.only()).findUserResponseDtoById(anyUserId);
        Mockito.verify(userRepository, Mockito.times(1)).findUserResponseDtoById(anyUserId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void test_T0050_PS01_deleteUserById() {
        Mockito.when(userRepository.findUserResponseDtoById(anyUserId))
                .thenReturn(Optional.of(userResponseDto));

        UserResponseDto deleted = userService.deleteUserById(anyUserId);
        assertEquals(deleted, userResponseDto);

        Mockito.verify(userRepository, Mockito.times(1)).findUserResponseDtoById(anyUserId);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(anyUserId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void test_T0050_NS01_deleteUserById_noUserWithId() {
        Mockito.when(userRepository.findUserResponseDtoById(anyUserId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(anyUserId));

        Mockito.verify(userRepository, Mockito.only()).findUserResponseDtoById(anyUserId);
        Mockito.verify(userRepository, Mockito.times(1)).findUserResponseDtoById(anyUserId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}
