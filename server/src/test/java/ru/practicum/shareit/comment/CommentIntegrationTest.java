package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.repository.UserRepository;

//  Проверен в ItemIntegrationTest
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class CommentIntegrationTest {
    private final UserController userController;
    private final ItemController itemController;
    private final BookingController bookingController;
    private final RequestController requestController;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

//    @BeforeEach
//    public void preTestInitialization() {
//    }
//
//    @Test
//    public void test_T0010_PS01() {
//    }
//
//    @Nested
//    @TestMethodOrder(MethodOrderer.MethodName.class)
//    class OrderedIntegrationTest {
//        private void assertRepositorySize(long uQ, long iQ, long bQ, long rQ, long cQ) {
//
//            assertEquals(uQ, userRepository.count());
//            assertEquals(iQ, itemRepository.count());
//            assertEquals(bQ, bookingRepository.count());
//            assertEquals(rQ, requestRepository.count());
//            assertEquals(cQ, commentRepository.count());
//        }
//
//        @Test
//        public void test_T1010_PS01() {
//        }
//    }
}
