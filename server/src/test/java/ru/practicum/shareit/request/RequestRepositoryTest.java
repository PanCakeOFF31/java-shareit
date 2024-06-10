package ru.practicum.shareit.request;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class RequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private CommentRepository commentRepository;

    private User requester1;
    private Request request1;

    private User requester2;
    private Request request2;
    private Request request3;

    private User requester3;
    private Request request4;

    private static LocalDateTime ldt;

    @BeforeAll
    public static void setLocalDateTime() {
        ldt = LocalDateTime.now().withNano(0);
    }

    @BeforeEach
    public void preTestInitialization() {

        requester1 = User.builder()
                .name("user1-name")
                .email("user1-@mail.ru")
                .build();
        request1 = Request.builder()
                .description("request1-description")
                .created(ldt.minusHours(10))
                .build();

        requester2 = User.builder()
                .name("user2-name")
                .email("user2-@mail.ru")
                .build();
        request2 = Request.builder()
                .description("request2-description")
                .created(ldt.minusHours(6))
                .build();
        request3 = Request.builder()
                .description("request3-description")
                .created(ldt.minusHours(3))
                .build();

        requester3 = User.builder()
                .name("user3-name")
                .email("user3-@mail.ru")
                .build();
        request4 = Request.builder()
                .description("request3-description")
                .created(ldt.minusHours(9))
                .build();
    }

    private void assertRepositorySize(long uQ, long iQ, long bQ, long rQ, long cQ) {
        assertEquals(uQ, userRepository.count());
        assertEquals(iQ, itemRepository.count());
        assertEquals(bQ, bookingRepository.count());
        assertEquals(rQ, requestRepository.count());
        assertEquals(cQ, commentRepository.count());
    }

    @Nested
    @TestMethodOrder(MethodOrderer.MethodName.class)
    class RepositoryWithWithFilledDataBase {

        @BeforeEach
        public void fillRepositoryWithAssigningId() {
            assertRepositorySize(0, 0, 0, 0, 0);

            requester1 = userRepository.save(requester1);
            request1.setRequester(requester1);
            requestRepository.save(request1);

            requester2 = userRepository.save(requester2);
            request2.setRequester(requester2);
            requestRepository.save(request2);
            request3.setRequester(requester2);
            requestRepository.save(request3);

            requester3 = userRepository.save(requester3);
            request4.setRequester(requester3);
            requestRepository.save(request4);

            assertRepositorySize(3, 0, 0, 4, 0);
        }

        @Test
        public void test_T1010_PS01() {
            Optional<Request> anyFoundReq = requestRepository.findOne(Example.of(request2));
            assertTrue(anyFoundReq.isPresent());

            long requesterId = anyFoundReq.get().getRequester().getId();

            List<Request> all = requestRepository.findAllByRequesterIdOrderByCreatedDesc(requesterId, Pageable.ofSize(5));
            assertNotNull(all);
            assertEquals(2, all.size());

            Request newestRequest = all.get(0);
            Request oldestRequest = all.get(1);
            assertEquals(request3.getCreated(), newestRequest.getCreated());
            assertEquals(request2.getCreated(), oldestRequest.getCreated());

            List<Request> allInclusive = requestRepository.findAllByRequesterIdNot(requesterId, Pageable.ofSize(5));
            assertNotNull(allInclusive);
            assertEquals(2, allInclusive.size());
        }
    }
}