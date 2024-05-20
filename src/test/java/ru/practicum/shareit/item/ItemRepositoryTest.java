package ru.practicum.shareit.item;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingOrderResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Rollback
@Transactional(propagation = Propagation.REQUIRED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ItemRepositoryTest {
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

    //    1 Owner with 1 Item without request
    private User owner1;
    private Item item1;

    //    1 Owner with 2 Items without request
    private User owner2;
    private Item item2;
    private Item item3;

    //    1 Requester with 1 Request -> 1 Owner with 2 RequestItem
    private User requester1;
    private Request request1;
    private User owner3;
    private Item reqItem1;
    private Item reqItem2;

    //   1 Requester with 1 Request -> 2 Owner + 2 RequestItem
    private User requester2;
    private Request request2;
    private User owner4;
    private Item reqItem3;
    private User owner5;
    private Item reqItem4;

    private static LocalDateTime ldt;

    //    2 Booker with 2 Booking for Item1
    private User booker1; // Last booking
    private Booking booking1;
    private User booker2; // Next booking
    private Booking booking2;
    //    1 Booker with 1 Booking for Item2
    private User booker3; // Only last booking
    private Booking booking3;

    @BeforeAll
    public static void setLocalDateTime() {
        ldt = LocalDateTime.now().withNano(0);
    }

    @BeforeEach
    public void preTestInitialization() {

        owner1 = User.builder()
                .name("user1-name")
                .email("user1-@mail.ru")
                .build();
        item1 = Item.builder()
                .name("item1-name")
                .description("item1-description")
                .available(true)
                .comments(new ArrayList<>())
                .request(null)
                .build();

        owner2 = User.builder()
                .name("user2-name")
                .email("user2-@mail.ru")
                .build();
        item2 = Item.builder()
                .name("item2-name")
                .description("item2-description")
                .available(true)
                .comments(new ArrayList<>())
                .request(null)
                .build();
        item3 = Item.builder()
                .name("item3-name")
                .description("item3-description")
                .available(true)
                .comments(new ArrayList<>())
                .request(null)
                .build();

        requester1 = User.builder()
                .name("user3-name")
                .email("user3-@mail.ru")
                .build();
        request1 = Request.builder()
                .description("request1-description")
                .created(LocalDateTime.now())
                .build();
        owner3 = User.builder()
                .name("user4-name")
                .email("user4-@mail.ru")
                .build();
        reqItem1 = Item.builder()
                .name("item4-name")
                .description("item4-description")
                .available(true)
                .comments(new ArrayList<>())
                .build();
        reqItem2 = Item.builder()
                .name("item5-name")
                .description("item5-description")
                .available(true)
                .comments(new ArrayList<>())
                .build();

        requester2 = User.builder()
                .name("user5-name")
                .email("user5-@mail.ru")
                .build();
        request2 = Request.builder()
                .description("request2-description")
                .created(LocalDateTime.now())
                .build();
        owner4 = User.builder()
                .name("user6-name")
                .email("user6-@mail.ru")
                .build();
        reqItem3 = Item.builder()
                .name("item6-name")
                .description("item6-description")
                .available(true)
                .comments(new ArrayList<>())
                .build();
        owner5 = User.builder()
                .name("user7-name")
                .email("user7-@mail.ru")
                .build();
        reqItem4 = Item.builder()
                .name("item7-name")
                .description("item7-description")
                .available(true)
                .comments(new ArrayList<>())
                .build();

        booker1 = User.builder()
                .name("user8-name")
                .email("user8-@mail.ru")
                .build();
        booker2 = User.builder()
                .name("user9-name")
                .email("user9-@mail.ru")
                .build();
        booking1 = Booking.builder()
                .start(ldt.minusHours(2))
                .end(ldt.minusHours(1))
                .status(Status.APPROVED)
                .build();
        booking2 = Booking.builder()
                .start(ldt.plusHours(1))
                .end(ldt.plusHours(2))
                .status(Status.APPROVED)
                .build();
        booker3 = User.builder()
                .name("user10-name")
                .email("user10-@mail.ru")
                .build();
        booking3 = Booking.builder()
                .start(ldt.minusHours(1))
                .end(ldt.plusHours(1))
                .status(Status.APPROVED)
                .build();
    }

    private void assertRepositorySize(long uQ, long iQ, long bQ, long rQ, long cQ) {
        assertEquals(uQ, userRepository.count());
        assertEquals(iQ, itemRepository.count());
        assertEquals(bQ, bookingRepository.count());
        assertEquals(rQ, requestRepository.count());
        assertEquals(cQ, commentRepository.count());
    }

    @Test
    public void test_T0010_PS01_save() {
        User savedUser = userRepository.save(owner1);

        item1.setOwner(savedUser);
        Item savedItem = itemRepository.save(item1);

        long ownerId = savedUser.getId();
        long itemId = savedItem.getId();

        assertTrue(itemRepository.existsItemByIdAndOwnerId(itemId, ownerId));
        assertTrue(itemRepository.findItemByIdAndOwnerId(itemId, ownerId).isPresent());
        assertEquals(itemRepository.findItemByIdAndOwnerId(itemId, ownerId).get(), savedItem);

        assertRepositorySize(1, 1, 0, 0, 0);
    }

    @Test
    public void test_T0020_PS01_findAllByOwnerIdOrderByIdAsc() {
        User savedUser1 = userRepository.save(owner1);
        User savedUser2 = userRepository.save(owner2);

        item1.setOwner(savedUser1);
        Item savedItem1 = itemRepository.save(item1);

        item2.setOwner(savedUser2);
        Item savedItem2 = itemRepository.save(item2);

        item3.setOwner(savedUser2);
        Item savedItem3 = itemRepository.save(item3);

        long ownerId1 = savedUser1.getId();
        long itemId1 = savedItem1.getId();
        long ownerId2 = savedUser2.getId();
        long itemId2 = savedItem2.getId();
        long itemId3 = savedItem3.getId();

        assertEquals(1, itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId1, Pageable.ofSize(5)).size());
        assertEquals(2, itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId2, Pageable.ofSize(5)).size());
        assertEquals(1, itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId2, Pageable.ofSize(1)).size());

        assertRepositorySize(2, 3, 0, 0, 0);
    }

    @Test
    public void test_T0030_PS01_findAllByRequestId() {
        User savedRequester = userRepository.save(requester1);
        User savedOwner = userRepository.save(owner3);

        request1.setRequester(savedRequester);
        Request savedRequest = requestRepository.save(request1);

        reqItem1.setOwner(savedOwner);
        reqItem1.setRequest(savedRequest);
        Item savedItem = itemRepository.save(reqItem1);

        assertEquals(1, itemRepository.findAllByRequestId(savedItem.getRequest().getId(), Pageable.ofSize(5)).size());
        assertRepositorySize(2, 1, 0, 1, 0);
    }

    @Test
    public void test_T0040_PS01_findItemsByNameOrDescriptionTextAndIsAvailable() {
        User savedUser1 = userRepository.save(owner1);
        item1.setOwner(savedUser1);
        itemRepository.save(item1);

        User savedUser2 = userRepository.save(owner2);
        item2.setOwner(savedUser2);
        itemRepository.save(item2);
        item3.setOwner(savedUser2);
        itemRepository.save(item3);

        User savedRequester = userRepository.save(requester1);
        User savedOwner = userRepository.save(owner3);
        request1.setRequester(savedRequester);
        Request savedRequest = requestRepository.save(request1);
        reqItem1.setOwner(savedOwner);
        reqItem1.setRequest(savedRequest);
        itemRepository.save(reqItem1);

        assertEquals(4, itemRepository.findItemsByNameOrDescriptionTextAndIsAvailable("description", 0, 10).size());
        assertEquals(1, itemRepository.findItemsByNameOrDescriptionTextAndIsAvailable("item2-", 0, 10).size());
        assertRepositorySize(4, 4, 0, 1, 0);
    }

    @Nested
    @Rollback
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class RepositoryWithWithFilledDataBaseWithRollBack {

        @BeforeEach
        public void fillRepositoryWithAssigningId() {
            owner1 = userRepository.save(owner1);
            item1.setOwner(owner1);
            item1 = itemRepository.save(item1);

            owner2 = userRepository.save(owner2);
            item2.setOwner(owner2);
            item2 = itemRepository.save(item2);
            item3.setOwner(owner2);
            item3 = itemRepository.save(item3);

            requester1 = userRepository.save(requester1);
            request1.setRequester(requester1);
            request1 = requestRepository.save(request1);
            owner3 = userRepository.save(owner3);
            reqItem1.setOwner(owner3);
            reqItem1.setRequest(request1);
            reqItem2.setOwner(owner3);
            reqItem2.setRequest(request1);
            reqItem1 = itemRepository.save(reqItem1);
            reqItem2 = itemRepository.save(reqItem2);

            requester2 = userRepository.save(requester2);
            request2.setRequester(requester2);
            request2 = requestRepository.save(request2);

            owner4 = userRepository.save(owner4);
            reqItem3.setOwner(owner4);
            reqItem3.setRequest(request2);
            owner5 = userRepository.save(owner5);
            reqItem4.setOwner(owner5);
            reqItem4.setRequest(request2);
            reqItem3 = itemRepository.save(reqItem3);
            reqItem4 = itemRepository.save(reqItem4);

            booker1 = userRepository.save(booker1);
            booking1.setItem(item1);
            booking1.setBooker(booker1);
            booking1 = bookingRepository.save(booking1);

            booker2 = userRepository.save(booker2);
            booking2.setItem(item1);
            booking2.setBooker(booker2);
            booking2 = bookingRepository.save(booking2);

            booker3 = userRepository.save(booker3);
            booking3.setItem(item2);
            booking3.setBooker(booker3);
            booking3 = bookingRepository.save(booking3);

            assertRepositorySize(10, 7, 3, 2, 0);
        }

        @Test
        public void test_T1010_PS01_findAllByRequestId() {
            List<Item> items = itemRepository.findAllByRequestId(reqItem4.getRequest().getId(), Pageable.ofSize(100));
            assertEquals(2, items.size());
        }

        @Test
        @DisplayName("Last booking")
        public void test_T1020_PS01_findTopBookingItemByItemIdAndEndLessThanEqualOrderByEndDesc() {
            List<BookingOrderResponseDto> bookings = itemRepository
                    .findTopBookingItemByItemIdAndEndLessThanEqualOrderByEndDesc(item1.getId(), LocalDateTime.now(), Pageable.ofSize(10));
            assertEquals(1, bookings.size());

            BookingOrderResponseDto lastBooking = bookings.get(0);
            assertEquals(booking1.getId(), lastBooking.getId());
            assertEquals(booker1.getId(), lastBooking.getBookerId());
            assertEquals(booking1.getStart(), lastBooking.getStart());
            assertEquals(booking1.getEnd(), lastBooking.getEnd());
        }

        @Test
        @DisplayName("Only last booking")
        public void test_T1030_PS01_findTopBookingItemByItemIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByEndDesc() {
            List<BookingOrderResponseDto> bookings = itemRepository
                    .findTopBookingItemByItemIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByEndDesc(item2.getId(), LocalDateTime.now(), Pageable.ofSize(10));
            assertEquals(1, bookings.size());

            BookingOrderResponseDto lastBooking = bookings.get(0);
            assertEquals(booking3.getId(), lastBooking.getId());
            assertEquals(booker3.getId(), lastBooking.getBookerId());
            assertEquals(booking3.getStart(), lastBooking.getStart());
            assertEquals(booking3.getEnd(), lastBooking.getEnd());
        }

        @Test
        @DisplayName("Next booking")
        public void test_T1040_PS01_findTopBookingItemByItemIdAndStartGreaterThanEqualAndStatusInOrderByStartAsc() {
            List<BookingOrderResponseDto> bookings = itemRepository
                    .findTopBookingItemByItemIdAndStartGreaterThanEqualAndStatusInOrderByStartAsc(item1.getId(), LocalDateTime.now(), List.of(Status.WAITING, Status.APPROVED), Pageable.ofSize(10));
            assertEquals(1, bookings.size());

            BookingOrderResponseDto lastBooking = bookings.get(0);
            assertEquals(booking2.getId(), lastBooking.getId());
            assertEquals(booker2.getId(), lastBooking.getBookerId());
            assertEquals(booking2.getStart(), lastBooking.getStart());
            assertEquals(booking2.getEnd(), lastBooking.getEnd());
        }
    }
}