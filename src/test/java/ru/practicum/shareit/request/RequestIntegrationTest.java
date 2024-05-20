package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.ReqGetDto;
import ru.practicum.shareit.request.dto.ReqRequestDto;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class RequestIntegrationTest {
    private final UserController userController;
    private final ItemController itemController;
    private final BookingController bookingController;
    private final RequestController requestController;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    private static LocalDateTime ldt;
    private int from;
    private int size;

    private UserRequestDto requester1;
    private ReqRequestDto request1;
    private static long requester1Id;
    private static long request1Id;

    private UserRequestDto requester2;
    private ReqRequestDto request2;
    private static long requester2Id;
    private static long request2Id;

    private UserRequestDto owner1;
    private ItemRequestDto reqItem1; // RequestItem1 for request1
    private ItemRequestDto reqItem2; // RequestItem1 for request2
    private static long owner1Id;
    private static long reqItem1Id;
    private static long reqItem2Id;

    private UserRequestDto owner2;
    private ItemRequestDto reqItem3;  // RequestItem1 for request1
    private ItemRequestDto reqItem4;  // RequestItem1 for request2
    private ItemRequestDto reqItem5;  // RequestItem1 for request2
    private static long owner2Id;
    private static long reqItem3Id;
    private static long reqItem4Id;
    private static long reqItem5Id;

    @BeforeAll
    public static void setLocalDateTime() {
        ldt = LocalDateTime.now().withNano(0);
    }

    @BeforeEach
    public void preTestInitialization() {
        requester1 = UserRequestDto.builder()
                .name("user1-name")
                .email("user1-@mail.ru")
                .build();
        request1 = ReqRequestDto.builder()
                .description("request1-description")
                .build();

        requester2 = UserRequestDto.builder()
                .name("user2-name")
                .email("user2-@mail.ru")
                .build();
        request2 = ReqRequestDto.builder()
                .description("request2-description")
                .build();

        owner1 = UserRequestDto.builder()
                .name("user3-name")
                .email("user3-@mail.ru")
                .build();
        reqItem1 = ItemRequestDto.builder()
                .name("item1-name")
                .description("item1-description")
                .available(true)
                .build();
        reqItem2 = ItemRequestDto.builder()
                .name("item2-name")
                .description("item2-description")
                .available(true)
                .build();

        owner2 = UserRequestDto.builder()
                .name("user4-name")
                .email("user4-@mail.ru")
                .build();
        reqItem3 = ItemRequestDto.builder()
                .name("item3-name")
                .description("item3-description")
                .available(true)
                .build();
        reqItem4 = ItemRequestDto.builder()
                .name("item4-name")
                .description("item4-description")
                .available(true)
                .build();
        reqItem5 = ItemRequestDto.builder()
                .name("item4-name")
                .description("item4-description")
                .available(true)
                .build();

        from = 0;
        size = 10;
    }


    private void assertRepositorySize(long uQ, long iQ, long bQ, long rQ, long cQ) {

        assertEquals(uQ, userRepository.count());
        assertEquals(iQ, itemRepository.count());
        assertEquals(bQ, bookingRepository.count());
        assertEquals(rQ, requestRepository.count());
        assertEquals(cQ, commentRepository.count());
    }


    @Test
    @Sql(scripts = "file:./src/test/java/resources/clear-db.sql", executionPhase = AFTER_TEST_METHOD)
    public void test_T0010_NS01_RequestNotFoundException() {
        assertRepositorySize(0, 0, 0, 0, 0);

        requester1Id = userController.createUser(requester1).getId();
        request1Id = requestController.createRequest(request1, requester1Id).getId();

        assertThrows(RequestNotFoundException.class, () -> requestController.getRequestByRequestId(requester1Id, request1Id + 1));
        assertRepositorySize(1, 0, 0, 1, 0);
    }

    @Test
    @Sql(scripts = "file:./src/test/java/resources/clear-db.sql", executionPhase = AFTER_TEST_METHOD)
    public void test_T0010_NS02_RequestNotFoundException() {
        assertRepositorySize(0, 0, 0, 0, 0);

        requester1Id = userController.createUser(requester1).getId();
        request1Id = requestController.createRequest(request1, requester1Id).getId();

        owner1Id = userController.createUser(owner1).getId();
        reqItem1.setRequestId(request1Id + 1);
        assertThrows(RequestNotFoundException.class, () -> itemController.createItem(reqItem1, owner1Id));

        assertThrows(RequestNotFoundException.class, () -> requestController.getRequestByRequestId(requester1Id, request1Id + 1));
        assertRepositorySize(2, 0, 0, 1, 0);
    }

    @Nested
    @TestMethodOrder(MethodOrderer.MethodName.class)
    class OrderedIntegrationTestWithoutRollback {

        @Test
        @Sql(scripts = "file:./src/test/java/resources/clear-db.sql", executionPhase = BEFORE_TEST_METHOD)
        public void test_T1010_PS01_fillData() throws InterruptedException {
            assertRepositorySize(0, 0, 0, 0, 0);

            requester1Id = userController.createUser(requester1).getId();
            request1Id = requestController.createRequest(request1, requester1Id).getId();

            TimeUnit.SECONDS.sleep(2);

            requester2Id = userController.createUser(requester2).getId();
            request2Id = requestController.createRequest(request2, requester2Id).getId();

            owner1Id = userController.createUser(owner1).getId();
            reqItem1.setRequestId(request1Id);
            reqItem2.setRequestId(request1Id);
            reqItem1Id = itemController.createItem(reqItem1, owner1Id).getId();
            reqItem2Id = itemController.createItem(reqItem2, owner1Id).getId();

            owner2Id = userController.createUser(owner2).getId();
            reqItem3.setRequestId(request2Id);
            reqItem4.setRequestId(request2Id);
            reqItem5.setRequestId(request2Id);
            reqItem3Id = itemController.createItem(reqItem3, owner2Id).getId();
            reqItem4Id = itemController.createItem(reqItem4, owner2Id).getId();
            reqItem5Id = itemController.createItem(reqItem5, owner2Id).getId();


            assertRepositorySize(4, 5, 0, 2, 0);
        }

        @Test
        public void test_T1020_PS01_getRequestsForRequester() {
            assertRepositorySize(4, 5, 0, 2, 0);

            List<ReqGetDto> requestsRequester1 = requestController.getRequestsForRequester(requester1Id, from, size);
            assertEquals(1, requestsRequester1.size());
            assertEquals(2, requestsRequester1.get(0).getItems().size());

            List<ReqGetDto> requestsRequester2 = requestController.getRequestsForRequester(requester2Id, from, size);
            assertEquals(1, requestsRequester2.size());
            assertEquals(3, requestsRequester2.get(0).getItems().size());
        }

        @Test
        public void test_T1030_PS01_getAllRequests() {
            assertRepositorySize(4, 5, 0, 2, 0);

            List<ReqGetDto> requestsRequester1 = requestController.getAllRequests(requester1Id, from, size);
            assertEquals(1, requestsRequester1.size());
            assertEquals(request2Id, requestsRequester1.get(0).getId());

            List<ReqGetDto> requestsRequester2 = requestController.getAllRequests(requester2Id, from, size);
            assertEquals(1, requestsRequester2.size());
            assertEquals(request1Id, requestsRequester2.get(0).getId());
        }

        @Test
        public void test_T1050_PS01_getRequestByRequestId() {
            assertRepositorySize(4, 5, 0, 2, 0);

            ReqGetDto gotRequest1 = requestController.getRequestByRequestId(requester1Id, request1Id);
            assertNotNull(gotRequest1);
            assertEquals(2, gotRequest1.getItems().size());
            assertEquals(request1.getDescription(), gotRequest1.getDescription());


            ReqGetDto gotRequest2 = requestController.getRequestByRequestId(requester2Id, request2Id);
            assertNotNull(gotRequest2);
            assertEquals(3, gotRequest2.getItems().size());
            assertEquals(request2.getDescription(), gotRequest2.getDescription());
        }
    }
}
