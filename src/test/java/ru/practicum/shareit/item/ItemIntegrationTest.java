package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.UserNotBookedItemException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.ReqCreateDto;
import ru.practicum.shareit.request.dto.ReqRequestDto;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ItemIntegrationTest {
    private final UserController userController;
    private final ItemController itemController;
    private final BookingController bookingController;
    private final RequestController requestController;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    //    1 Owner with 1 Item without Request
    private UserRequestDto owner1;
    private ItemRequestDto item1;

    //    1 Owner with 2 Item without Request
    private UserRequestDto owner2;
    private ItemRequestDto item2;
    private ItemRequestDto item3;

    //    1 Requester with 1 Request and 1 Owner with 2 RequestItem
    private UserRequestDto requester1;
    private ReqRequestDto request1;
    private UserRequestDto owner3;
    private ItemRequestDto reqItem1;
    private ItemRequestDto reqItem2;

    //    2 Booker + 2 Booking for Item1
    private UserRequestDto booker1; // Last booking
    private BookingRequestDto booking1;
    private CommentRequestDto comment1;
    private UserRequestDto booker2; // Next booking
    private BookingRequestDto booking2;
    private CommentRequestDto comment2;

    private static LocalDateTime ldt;

    @BeforeAll
    public static void setLocalDateTime() {
        ldt = LocalDateTime.now().withNano(0);
    }

    @BeforeEach
    public void preTestInitialization() {
        owner1 = UserRequestDto.builder()
                .name("user1-name")
                .email("user1-@mail.ru")
                .build();
        item1 = ItemRequestDto.builder()
                .name("item1-name")
                .description("item1-description")
                .available(true)
                .requestId(null)
                .build();

        owner2 = UserRequestDto.builder()
                .name("user2-name")
                .email("user2-@mail.ru")
                .build();
        item2 = ItemRequestDto.builder()
                .name("item2-name")
                .description("item2-description")
                .available(true)
                .requestId(null)
                .build();
        item3 = ItemRequestDto.builder()
                .name("item3-name")
                .description("item3-description")
                .available(true)
                .requestId(null)
                .build();

        requester1 = UserRequestDto.builder()
                .name("user3-name")
                .email("user3-@mail.ru")
                .build();
        request1 = ReqRequestDto.builder()
                .description("request1-description")
                .build();
        owner3 = UserRequestDto.builder()
                .name("user4-name")
                .email("user4-@mail.ru")
                .build();
        reqItem1 = ItemRequestDto.builder()
                .name("item3-name")
                .description("item3-description")
                .available(true)
                .build();
        reqItem2 = ItemRequestDto.builder()
                .name("item4-name")
                .description("item4-description")
                .available(false)
                .build();

        booker1 = UserRequestDto.builder()
                .name("user5-name")
                .email("user5-@mail.ru")
                .build();
        booking1 = BookingRequestDto.builder()
                .build();
        booker2 = UserRequestDto.builder()
                .name("user6-name")
                .email("user6-@mail.ru")
                .build();
        booking2 = BookingRequestDto.builder()
                .start(ldt.plusHours(3))
                .end(ldt.plusHours(4))
                .build();

        comment1 = CommentRequestDto.builder()
                .text("comment-1-comment")
                .build();

        comment2 = CommentRequestDto.builder()
                .text("comment-2-comment")
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
    class OrderedIntegrationTestWithoutRollback {

        @Test
        public void test_T1010_PS01_create_Owner1Item1() {
            UserResponseDto createdOwner1 = userController.createUser(owner1);
            ItemResponseDto createdItem1 = itemController.createItem(item1, createdOwner1.getId());

            assertRepositorySize(1, 1, 0, 0, 0);
            assertEquals(item1.getDescription(), createdItem1.getDescription());

            ItemResponseDto gotItem = itemController.getItem(createdOwner1.getId(), createdItem1.getId());

            assertEquals(createdItem1.getName(), gotItem.getName());
            assertEquals(createdItem1.getDescription(), gotItem.getDescription());
            assertEquals(createdItem1.getAvailable(), gotItem.getAvailable());
            assertNull(gotItem.getLastBooking());
            assertNull(gotItem.getNextBooking());
            assertTrue(gotItem.getComments().isEmpty());

            assertThrows(UserNotBookedItemException.class,
                    () -> itemController.createComment(comment1, createdItem1.getId(), createdItem1.getId()));
        }

        @Test
        public void test_T1020_PS01_create_Owner2Item2Item3() {
            UserResponseDto createdOwner2 = userController.createUser(owner2);
            ItemResponseDto createdItem2 = itemController.createItem(item2, createdOwner2.getId());
            ItemResponseDto createdItem3 = itemController.createItem(item3, createdOwner2.getId());

            assertRepositorySize(2, 3, 0, 0, 0);
            assertEquals(item2.getName(), createdItem2.getName());
            assertEquals(item3.getRequestId(), createdItem3.getRequestId());

            ItemRequestDto itemToUpdate = item3.toBuilder().name("updated-item3-name").available(false).build();
            ItemResponseDto updateItem = itemController.updateItem(itemToUpdate, createdOwner2.getId(), createdItem3.getId());

            assertEquals(itemToUpdate.getName(), updateItem.getName());
            assertEquals(itemToUpdate.getDescription(), updateItem.getDescription());
            assertEquals(itemToUpdate.getAvailable(), updateItem.getAvailable());
            assertEquals(itemToUpdate.getRequestId(), updateItem.getRequestId());

            List<ItemResponseDto> items = itemController.getItemsByOwner(createdItem2.getId(), 0, 10);
            assertFalse(items.isEmpty());
            assertEquals(2, items.size());
        }

        @Test
        public void test_T1030_PS01_searchItems() {
            assertRepositorySize(2, 3, 0, 0, 0);

            List<ItemResponseDto> items = itemController.searchItems(-1L, "item", 0, 10);
            assertFalse(items.isEmpty());
            assertEquals(2, items.size());
        }


        @Test
        public void test_T1040_PS01_create_Requester1Request1_Owner3ReqItem1ReqItem2() {
            UserResponseDto createdRequester1 = userController.createUser(requester1);
            ReqCreateDto createdRequest1 = requestController.createRequest(request1, createdRequester1.getId());

            UserResponseDto createOwner3 = userController.createUser(owner3);
            reqItem1.setRequestId(createdRequest1.getId());
            ItemResponseDto createdReqItem1 = itemController.createItem(reqItem1, createOwner3.getId());
            reqItem2.setRequestId(createdRequest1.getId());
            ItemResponseDto createdReqItem2 = itemController.createItem(reqItem2, createOwner3.getId());

            assertRepositorySize(4, 5, 0, 1, 0);

            assertEquals(reqItem1.getRequestId(), createdReqItem1.getRequestId());
            assertEquals(reqItem1.getAvailable(), createdReqItem1.getAvailable());
            assertEquals(reqItem2.getRequestId(), createdReqItem2.getRequestId());
            assertNull(createdReqItem2.getNextBooking());
        }

        @Test
        public void test_T1050_PS01_createBooking_Booking1Booker1_Booking2Booker2() throws InterruptedException {
            Item anyItem = itemRepository.findAll().get(0);
            long anyItemId = anyItem.getId();
            long anyItemOwnerId = anyItem.getOwner().getId();

            UserResponseDto createdBooker1 = userController.createUser(booker1);
            booking1.setItemId(anyItemId);
            booking1.setStart(LocalDateTime.now().plusSeconds(1));
            booking1.setEnd(LocalDateTime.now().plusSeconds(2));
            BookingResponseDto createdBooking1 = bookingController.createBooking(booking1, createdBooker1.getId());
            TimeUnit.SECONDS.sleep(2);

            UserResponseDto createdBooker2 = userController.createUser(booker2);
            booking2.setItemId(anyItemId);

            BookingResponseDto createdBooking2 = bookingController.createBooking(booking2, createdBooker2.getId());

            ItemResponseDto gotItem = itemController.getItem(anyItemId, anyItemOwnerId);
            assertRepositorySize(6, 5, 2, 1, 0);

            assertNotNull(gotItem.getNextBooking());
            assertNotNull(gotItem.getLastBooking());

            CommentResponseDto createdComemnt1 = itemController.createComment(comment1, createdBooker1.getId(), anyItemId);
            assertThrows(UserNotBookedItemException.class, () -> itemController.createComment(comment2, createdBooker2.getId(), anyItemId));

            assertRepositorySize(6, 5, 2, 1, 1);
        }
    }
}
