package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingItemOwnerIncorrectException;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.RequestController;
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
public class BookingIntegrationTest {
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
    private static long owner1Id;
    private static long item1Id;

    //    1 Requester with 1 Request and 2 Owner with 3 RequestItem
    private UserRequestDto requester1;
    private ReqRequestDto request1;
    private UserRequestDto owner2;
    private ItemRequestDto reqItem1;
    private UserRequestDto owner3;
    private ItemRequestDto reqItem2;
    private ItemRequestDto reqItem3;
    private static long requester1Id;
    private static long request1Id;
    private static long owner2Id;
    private static long reqItem1Id;
    private static long owner3Id;
    private static long reqItem2Id;
    private static long reqItem3Id;

    //    1 Booker + 3 Booking
    private UserRequestDto booker1;
    private BookingRequestDto booking1; // Booking for Item1 LastBooking
    private BookingRequestDto booking2; // Booking for Item1 NextBooking
    private BookingRequestDto booking3; // Booking for RequestItem1 NextBooking
    private static long booker1Id;
    private static long booking1Id;
    private static long booking2Id;
    private static long booking3Id;

    //    1 Booker + 2 Booking
    private UserRequestDto booker2;
    private BookingRequestDto booking4; // Booking for ReqItem2 NextBooking
    private BookingRequestDto booking5; // Booking for ReqItem3 OnlyLastBooking
    private BookingRequestDto booking6; // Booking for ReqItem2 NextBooking to be REJECTED
    private static long booker2Id;
    private static long booking4Id;
    private static long booking5Id;
    private static long booking6Id;

    private static LocalDateTime ldt;
    private int from;
    private int size;

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

        requester1 = UserRequestDto.builder()
                .name("user2-name")
                .email("user2-@mail.ru")
                .build();
        request1 = ReqRequestDto.builder()
                .description("request1-description")
                .build();
        owner2 = UserRequestDto.builder()
                .name("user3-name")
                .email("user3-@mail.ru")
                .build();
        reqItem1 = ItemRequestDto.builder()
                .name("item2-name")
                .description("item2-description")
                .available(true)
                .build();
        owner3 = UserRequestDto.builder()
                .name("user4-name")
                .email("user4-@mail.ru")
                .build();
        reqItem2 = ItemRequestDto.builder()
                .name("item3-name")
                .description("item3-description")
                .available(true)
                .build();
        reqItem3 = ItemRequestDto.builder()
                .name("item4-name")
                .description("item4-description")
                .available(true)
                .build();

        booker1 = UserRequestDto.builder()
                .name("user5-name")
                .email("user5-@mail.ru")
                .build();
        booking1 = BookingRequestDto.builder()
                .build();
        booking2 = BookingRequestDto.builder()
                .start(ldt.plusHours(1))
                .end(ldt.plusHours(19))
                .build();
        booking3 = BookingRequestDto.builder()
                .start(ldt.plusHours(5))
                .end(ldt.plusHours(41))
                .build();

        booker2 = UserRequestDto.builder()
                .name("user6-name")
                .email("user6-@mail.ru")
                .build();
        booking4 = BookingRequestDto.builder()
                .start(ldt.plusHours(2))
                .end(ldt.plusHours(4))
                .build();
        booking5 = BookingRequestDto.builder()
                .end(ldt.plusHours(10))
                .build();
        booking6 = BookingRequestDto.builder()
                .start(ldt.plusHours(7))
                .end(ldt.plusHours(8))
                .build();

        from = 0;
        size = 10;
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
        public void test_T1010_PS01_fillData() throws InterruptedException {
            assertRepositorySize(0, 0, 0, 0, 0);

            owner1Id = userController.createUser(owner1).getId();
            item1Id = itemController.createItem(item1, owner1Id).getId();

            requester1Id = userController.createUser(requester1).getId();
            request1Id = requestController.createRequest(request1, requester1Id).getId();

            owner2Id = userController.createUser(owner2).getId();
            reqItem1.setRequestId(request1Id);
            reqItem1Id = itemController.createItem(reqItem1, owner2Id).getId();

            owner3Id = userController.createUser(owner3).getId();
            reqItem2.setRequestId(request1Id);
            reqItem2Id = itemController.createItem(reqItem2, owner3Id).getId();
            reqItem3.setRequestId(request1Id);
            reqItem3Id = itemController.createItem(reqItem3, owner3Id).getId();

            booker1Id = userController.createUser(booker1).getId();
            booking1.setItemId(item1Id);
            booking1.setStart(LocalDateTime.now().plusSeconds(1));
            booking1.setEnd(LocalDateTime.now().plusSeconds(2));
            booking1Id = bookingController.createBooking(booking1, booker1Id).getId();
            TimeUnit.SECONDS.sleep(2);
            booking2.setItemId(item1Id);
            booking2Id = bookingController.createBooking(booking2, booker1Id).getId();
            booking3.setItemId(reqItem1Id);
            booking3Id = bookingController.createBooking(booking3, booker1Id).getId();

            booker2Id = userController.createUser(booker2).getId();
            booking4.setItemId(reqItem2Id);
            booking4Id = bookingController.createBooking(booking4, booker2Id).getId();
            booking5.setItemId(reqItem3Id);
            booking5.setStart(LocalDateTime.now().plusSeconds(1));
            booking5Id = bookingController.createBooking(booking5, booker2Id).getId();
            TimeUnit.SECONDS.sleep(2);
            booking6.setItemId(reqItem2Id);
            booking6Id = bookingController.createBooking(booking6, booker2Id).getId();

            assertRepositorySize(6, 4, 6, 1, 0);
        }

        @Test
        public void test_T1020_PS01_validateData() {
            UserResponseDto gotBooker1 = userController.getUserById(booker1Id);
            assertNotNull(gotBooker1);
            assertEquals(booker1.getEmail(), gotBooker1.getEmail());

            ItemResponseDto gotReqItem2 = itemController.getItem(owner3Id, reqItem2Id);
            assertNotNull(gotReqItem2);
            assertEquals(request1Id, gotReqItem2.getRequestId());
            assertEquals(booking4Id, gotReqItem2.getNextBooking().getId());
            assertNull(gotReqItem2.getLastBooking());

            List<ItemResponseDto> gotItems = itemController.getItemsByOwner(owner3Id, 0, 10);
            assertNotNull(gotItems);
            assertEquals(2, gotItems.size());

            assertRepositorySize(6, 4, 6, 1, 0);
        }

        @Test
        public void test_T1030_PS01_getBooking() {
            BookingResponseDto gotBooking2 = bookingController.getBooking(booker1Id, booking2Id);

            assertEquals(booking2Id, gotBooking2.getId());
            assertEquals(item1Id, gotBooking2.getItem().getId());
            assertEquals(booker1Id, gotBooking2.getBooker().getId());
            assertEquals(booking2.getStart(), gotBooking2.getStart());
            assertEquals(booking2.getEnd(), gotBooking2.getEnd());
            assertEquals(Status.WAITING, gotBooking2.getStatus());

            BookingResponseDto gotBooking2AfterToBook = bookingController.toBook(owner1Id, booking2Id, true);
            assertEquals(booking2Id, gotBooking2AfterToBook.getId());
            assertEquals(item1Id, gotBooking2AfterToBook.getItem().getId());
            assertEquals(booker1Id, gotBooking2AfterToBook.getBooker().getId());
            assertEquals(booking2.getStart(), gotBooking2AfterToBook.getStart());
            assertEquals(booking2.getEnd(), gotBooking2AfterToBook.getEnd());
            assertEquals(Status.APPROVED, gotBooking2AfterToBook.getStatus());

            assertThrows(BookingItemOwnerIncorrectException.class, () -> bookingController.toBook(requester1Id, booking2Id, true));

            BookingResponseDto gotBooking6AfterReject = bookingController.toBook(owner3Id, booking6Id, false);
            assertEquals(booking6Id, gotBooking6AfterReject.getId());
            assertEquals(reqItem2Id, gotBooking6AfterReject.getItem().getId());
            assertEquals(booker2Id, gotBooking6AfterReject.getBooker().getId());
            assertEquals(booking6.getStart(), gotBooking6AfterReject.getStart());
            assertEquals(booking6.getEnd(), gotBooking6AfterReject.getEnd());
            assertEquals(Status.REJECTED, gotBooking6AfterReject.getStatus());
        }


        @Test
        public void test_T1040_PS01_getAllBookingByBooker() {
            List<BookingResponseDto> bookingsAll = bookingController.getAllBookingByBooker(booker2Id, State.valueOf("ALL"), from, size);
            assertNotNull(bookingsAll);
            assertEquals(3, bookingsAll.size());

            List<BookingResponseDto> bookingsWaiting = bookingController.getAllBookingByBooker(booker2Id, State.valueOf("WAITING"), from, size);
            assertNotNull(bookingsWaiting);
            assertEquals(2, bookingsWaiting.size());

            List<BookingResponseDto> bookingsRejected = bookingController.getAllBookingByBooker(booker2Id, State.valueOf("REJECTED"), from, size);
            assertNotNull(bookingsRejected);
            assertEquals(1, bookingsRejected.size());
        }

        @Test
        public void test_T1050_PS01_getAllBookingByOwner() {
            List<BookingResponseDto> bookingsAll = bookingController.getAllBookingByOwner(owner3Id, State.valueOf("ALL"), from, size);
            assertNotNull(bookingsAll);
            assertEquals(3, bookingsAll.size());

            List<BookingResponseDto> bookingsApproved = bookingController.getAllBookingByOwner(owner3Id, State.valueOf("FUTURE"), from, size);
            assertNotNull(bookingsApproved);
            assertEquals(2, bookingsApproved.size());

            List<BookingResponseDto> bookingsRejected = bookingController.getAllBookingByOwner(owner3Id, State.valueOf("REJECTED"), from, size);
            assertNotNull(bookingsRejected);
            assertEquals(1, bookingsRejected.size());
        }

        @Test
        public void test_T1060_PS01_itemHasOnlyLastBooking() {
            ItemResponseDto gotItem = itemController.getItem(owner3Id, reqItem3Id);
            assertNotNull(gotItem.getLastBooking());
            assertEquals(booking5.getEnd(), gotItem.getLastBooking().getEnd());
            assertNull(gotItem.getNextBooking());
        }
    }
}
