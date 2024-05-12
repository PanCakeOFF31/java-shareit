package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Rollback
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UserRepositoryTest {

    private final TestEntityManager em;

    private final UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @BeforeEach
    public void preTestInitialization() {
        System.out.println(1);
        user1 = User.builder()
                .name("user1-name")
                .email("user1-@mail.ru")
                .build();

        user2 = User.builder()
                .name("user2-name")
                .email("user2-@mail.ru")
                .build();

        user3 = User.builder()
                .name("user3-name")
                .email("user3-@mail.ru")
                .build();

        user4 = User.builder()
                .name("user4-name")
                .email("user4-@mail.ru")
                .build();
    }

    @Nested
    class RepositoryWithWithFilledDataBase {

        private List<User> fillRepository() {
            user1 = userRepository.save(user1);
            user2 = userRepository.save(user2);
            user3 = userRepository.save(user3);
            user4 = userRepository.save(user4);

            var users = userRepository.findAll();
            assertEquals(4, users.size());

            Collections.shuffle(users);
            return users;
        }

        @Test
        public void test_T0020_PS01_findUserResponseDtoById() {
            List<User> users = fillRepository();

            User anyUser = users.get(0);
            long anyUserId = anyUser.getId();
            UserResponseDto anyUserAsDto = UserMapper.mapToUserResponseDto(anyUser);

            Optional<UserResponseDto> foundUser = userRepository.findUserResponseDtoById(anyUserId);
            assertTrue(foundUser.isPresent());
            assertEquals(anyUserAsDto, foundUser.get());
        }

        @Test
        public void test_T0030_PS01_findUserBookingDtoById() {
            List<User> users = fillRepository();

            User anyUser = users.get(0);
            long anyUserId = anyUser.getId();
            UserBookingDto anyUserAsDto = UserMapper.mapToUserBookingDto(anyUser);

            Optional<UserBookingDto> foundUser = userRepository.findUserBookingDtoById(anyUserId);
            assertTrue(foundUser.isPresent());
            assertEquals(anyUserAsDto, foundUser.get());
        }

        @Test
        public void test_T0030_PS01_findUserByEmail() {
            Optional<User> foundUser = userRepository.findUserByEmail(user3.getEmail());
            assertFalse(foundUser.isPresent());

            fillRepository();

            foundUser = userRepository.findUserByEmail(user3.getEmail());
            assertTrue(foundUser.isPresent());
            assertEquals(user3, foundUser.get());
        }
    }
}