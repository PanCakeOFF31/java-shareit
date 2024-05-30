package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class UserIntegrationTest {
    private final UserController userController;
    private final ItemController itemController;
    private final BookingController bookingController;
    private final RequestController requestController;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    private UserRequestDto user1;
    private UserRequestDto user2;
    private UserRequestDto user3;
    private UserRequestDto user4;
    private List<UserResponseDto> usersRightNow;

    @BeforeEach
    public void preTestInitialization() {
        user1 = UserRequestDto.builder()
                .name("user1-name")
                .email("emailuser1@mail.ru")
                .build();

        user2 = UserRequestDto.builder()
                .name("user2-name")
                .email("emailuser2@mail.ru")
                .build();

        user3 = UserRequestDto.builder()
                .name("user3-name")
                .email("emailuser3@mail.ru")
                .build();

        user4 = UserRequestDto.builder()
                .name("user4-name")
                .email("emailuser2@mail.ru")
                .build();

        usersRightNow = List.of();
    }

    @Nested
    @TestMethodOrder(MethodOrderer.MethodName.class)
    class OrderedIntegrationTestWithoutRollback {
        private void assertRepositorySize(long uQ, long iQ, long bQ, long rQ, long cQ) {

            assertEquals(uQ, userRepository.count());
            assertEquals(iQ, itemRepository.count());
            assertEquals(bQ, bookingRepository.count());
            assertEquals(rQ, requestRepository.count());
            assertEquals(cQ, commentRepository.count());
        }

        @Test
        public void test_T1010_PS01() {
            assertRepositorySize(0, 0, 0, 0, 0);
            UserResponseDto createdUser1 = userController.createUser(user1);
            usersRightNow = userController.getUsers();

            assertEquals(1, usersRightNow.size());
            assertEquals(createdUser1, usersRightNow.get(0));

            assertRepositorySize(1, 0, 0, 0, 0);
        }

        @Test
        public void test_T1020_PS01() {
            UserResponseDto createdUser2 = userController.createUser(user2);
            usersRightNow = userController.getUsers();

            assertEquals(2, usersRightNow.size());
            assertEquals(createdUser2, userController.getUserById(createdUser2.getId()));

            UserRequestDto userToUpdate = user2.toBuilder().name("updated-user2-name").build();
            UserResponseDto updatedUser = userController.updateUser(userToUpdate, createdUser2.getId());

            assertEquals(userToUpdate.getName(), updatedUser.getName());
            assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());

            usersRightNow = userController.getUsers();
            assertEquals(2, usersRightNow.size());

            assertRepositorySize(2, 0, 0, 0, 0);
        }

        @Test
        public void test_T1030_PS01() {
            List<Long> initialIds = usersRightNow.stream().map(UserResponseDto::getId).collect(Collectors.toList());

            UserResponseDto createdUser3 = userController.createUser(user3);
            usersRightNow = userController.getUsers();

            assertEquals(3, usersRightNow.size());
            assertEquals(createdUser3.toString(), userController.getUserById(createdUser3.getId()).toString());

            userController.deleteUserById(createdUser3.getId());

            usersRightNow = userController.getUsers();
            assertEquals(2, usersRightNow.size());

            assertTrue(usersRightNow.stream()
                    .map(UserResponseDto::getId)
                    .collect(Collectors.toList())
                    .containsAll(initialIds));

            assertRepositorySize(2, 0, 0, 0, 0);
        }

        @Test
        public void test_T1040_NS01() {
            assertThrows(DataIntegrityViolationException.class, () -> userController.createUser(user4));
        }
    }
}
