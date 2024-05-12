package ru.practicum.shareit.booking;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Rollback
@Transactional(propagation = Propagation.REQUIRED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BookingRepositoryTest {
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

    private static LocalDateTime ldt;
    private int from;
    private int size;
    private Pageable pageable;

    //    1 Owner with 1 Item without request
    private User owner1;
    private Item item1;
    private static long owner1Id;

    //    1 Owner with 1 Items without request
    private User owner2;
    private Item item2;
    private static long owner2Id;

    private User booker1;
    private Booking booking1; // booking for item1 LastBooking APPROVED
    private Booking booking2; // booking for item2 NextBooking APPROVED
    private static long booker1Id;

    private User booker2;
    private Booking booking3; // booking for item1 NextBooking APPROVED
    private Booking booking4; // booking for item2 LastBooking APPROVED
    private Booking booking5; // booking for item1 NextBooking APPROVED
    private Booking booking6; // booking for item1 NextBooking REJECTED
    private Booking booking7; // booking for item1 CurrentBooking APPROVED
    private static long booker2Id;

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

        booker1 = User.builder()
                .name("user3-name")
                .email("user3-@mail.ru")
                .build();
//        LastBooking
        booking1 = Booking.builder()
                .status(Status.APPROVED)
                .build();
        booking2 = Booking.builder()
                .start(ldt.plusMinutes(10))
                .end(ldt.plusMinutes(15))
                .status(Status.APPROVED)
                .build();

        booker2 = User.builder()
                .name("user4-name")
                .email("user4-@mail.ru")
                .build();
        booking3 = Booking.builder()
                .start(ldt.plusMinutes(20))
                .end(ldt.plusMinutes(25))
                .status(Status.APPROVED)
                .build();
//        LastBooking
        booking4 = Booking.builder()
                .status(Status.APPROVED)
                .build();
        booking5 = Booking.builder()
                .start(ldt.plusMinutes(30))
                .end(ldt.plusMinutes(40))
                .status(Status.APPROVED)
                .build();
        booking6 = Booking.builder()
                .start(ldt.plusMinutes(56))
                .end(ldt.plusMinutes(58))
                .status(Status.REJECTED)
                .build();
        booking7 = Booking.builder()
                .end(ldt.plusMinutes(5))
                .status(Status.APPROVED)
                .build();

        from = 0;
        size = 10;
        pageable = PageRequest.of(from > 0 ? from / size : 0, size);

    }

    private void assertRepositorySize(long uQ, long iQ, long bQ, long rQ, long cQ) {
        assertEquals(uQ, userRepository.count());
        assertEquals(iQ, itemRepository.count());
        assertEquals(bQ, bookingRepository.count());
        assertEquals(rQ, requestRepository.count());
        assertEquals(cQ, commentRepository.count());
    }

    @Nested
    @Rollback(value = false)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class RepositoryWithFilledDataBaseWithoutRollback {

        public void fillRepositoryWithAssigningId() throws InterruptedException {
            assertRepositorySize(0, 0, 0, 0, 0);

            owner1 = userRepository.save(owner1);
            owner1Id = owner1.getId();
            item1.setOwner(owner1);
            item1 = itemRepository.save(item1);

            owner2 = userRepository.save(owner2);
            owner2Id = owner2.getId();
            item2.setOwner(owner2);
            item2 = itemRepository.save(item2);

            booker1 = userRepository.save(booker1);
            booker1Id = booker1.getId();

            booking1.setStart(ldt.plusSeconds(1));
            booking1.setEnd(ldt.plusSeconds(2));
            booking1.setItem(item1);
            booking1.setBooker(booker1);
            booking1 = bookingRepository.save(booking1);

            booking2.setItem(item2);
            booking2.setBooker(booker1);
            booking2 = bookingRepository.save(booking2);

            booker2 = userRepository.save(booker2);
            booker2Id = booker2.getId();

            booking3.setItem(item1);
            booking3.setBooker(booker2);
            booking3 = bookingRepository.save(booking3);

            booking4.setStart(ldt.plusSeconds(1));
            booking4.setEnd(ldt.plusSeconds(2));
            booking4.setItem(item2);
            booking4.setBooker(booker2);
            booking4 = bookingRepository.save(booking4);

            booking5.setItem(item1);
            booking5.setBooker(booker2);
            booking5 = bookingRepository.save(booking5);

            booking6.setItem(item1);
            booking6.setBooker(booker2);
            booking6 = bookingRepository.save(booking6);

            booking7.setStart(ldt.plusSeconds(2));
            booking7.setItem(item1);
            booking7.setBooker(booker2);
            booking7 = bookingRepository.save(booking7);

            TimeUnit.SECONDS.sleep(2);
            assertRepositorySize(4, 2, 7, 0, 0);
        }

        @Test
        @Order(1)
        @DisplayName("repository filling")
        public void test_T1000_PS01_fillingRepository() throws InterruptedException {
            fillRepositoryWithAssigningId();
        }

        @Test
        @DisplayName("State.Past - byBookerId")
        public void test_T1010_PS01_findByBookerIdAndEndLessThanOrderByStartDesc() {
            List<Booking> booker1Bookings = bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(booker1Id, ldt.plusHours(1), pageable);
            assertNotNull(booker1Bookings);
            assertEquals(2, booker1Bookings.size());

            List<Booking> booker2Bookings = bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(booker2Id, ldt.plusMinutes(50), pageable);
            assertNotNull(booker2Bookings);
            assertEquals(4, booker2Bookings.size());
        }

        @Test
        @DisplayName("State.Current - byBookerId")
        public void test_T1020_PS01_findByBookerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc() {
            LocalDateTime now = LocalDateTime.now();

            List<Booking> booker1Bookings = bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(booker1Id, now, now, pageable);
            assertNotNull(booker1Bookings);
            assertEquals(0, booker1Bookings.size());

            List<Booking> booker2Bookings = bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(booker2Id, now, now, pageable);
            assertNotNull(booker2Bookings);
            assertEquals(1, booker2Bookings.size());
        }

        @Test
        @DisplayName("State.Future - byBookerId")
        public void test_T1030_PS01_findByBookerIdAndStartGreaterThanEqualOrderByStartDesc() {
            LocalDateTime now = LocalDateTime.now();

            List<Booking> booker1Bookings = bookingRepository.findByBookerIdAndStartGreaterThanEqualOrderByStartDesc(booker1Id, now, pageable);
            assertNotNull(booker1Bookings);
            assertEquals(1, booker1Bookings.size());

            List<Booking> booker2Bookings = bookingRepository.findByBookerIdAndStartGreaterThanEqualOrderByStartDesc(booker2Id, now, pageable);
            assertNotNull(booker2Bookings);
            assertEquals(3, booker2Bookings.size());
        }

        @Test
        @DisplayName("State.Status - byBookerId")
        public void test_T1040_PS01_findByBookerIdAndStatusOrderByStartDesc() {
            List<Booking> booker1Bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker1Id, Status.APPROVED, pageable);
            assertNotNull(booker1Bookings);
            assertEquals(2, booker1Bookings.size());

            booker1Bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker1Id, Status.CANCELED, pageable);
            assertNotNull(booker1Bookings);
            assertEquals(0, booker1Bookings.size());

            List<Booking> booker2Bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker2Id, Status.REJECTED, pageable);
            assertNotNull(booker2Bookings);
            assertEquals(1, booker2Bookings.size());

            booker2Bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker2Id, Status.APPROVED, pageable);
            assertNotNull(booker2Bookings);
            assertEquals(4, booker2Bookings.size());
        }

        @Test
        @DisplayName("State.All - byBookerId")
        public void test_T1050_PS01_findByBookerIdOrderByStartDesc() {
            List<Booking> booker1Bookings = bookingRepository.findByBookerIdOrderByStartDesc(booker1Id, pageable);
            assertNotNull(booker1Bookings);
            assertEquals(2, booker1Bookings.size());

            List<Booking> booker2Bookings = bookingRepository.findByBookerIdOrderByStartDesc(booker2Id, pageable);
            assertNotNull(booker2Bookings);
            assertEquals(5, booker2Bookings.size());
        }

        @Test
        @DisplayName("State.Past - byOwnerId")
        public void test_T1060_PS01_findByItemOwnerIdAndEndLessThanOrderByStartDesc() {
            List<Booking> owner1Bookings = bookingRepository.findByItemOwnerIdAndEndLessThanOrderByStartDesc(owner1Id, ldt.plusMinutes(27), pageable);
            assertNotNull(owner1Bookings);
            assertEquals(3, owner1Bookings.size());

            owner1Bookings = bookingRepository.findByItemOwnerIdAndEndLessThanOrderByStartDesc(owner1Id, ldt.plusMinutes(60), pageable);
            assertNotNull(owner1Bookings);
            assertEquals(5, owner1Bookings.size());

            List<Booking> owner2Bookings = bookingRepository.findByItemOwnerIdAndEndLessThanOrderByStartDesc(owner2Id, ldt.plusMinutes(10), pageable);
            assertNotNull(owner2Bookings);
            assertEquals(1, owner2Bookings.size());

            owner2Bookings = bookingRepository.findByItemOwnerIdAndEndLessThanOrderByStartDesc(owner2Id, ldt.plusMinutes(30), pageable);
            assertNotNull(owner2Bookings);
            assertEquals(2, owner2Bookings.size());
        }

        @Test
        @DisplayName("State.Current - byOwnerId")
        public void test_T1070_PS01_findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc() {
            LocalDateTime now = LocalDateTime.now();

            List<Booking> owner1Bookings = bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(owner1Id, now, now, pageable);
            assertNotNull(owner1Bookings);
            assertEquals(1, owner1Bookings.size());

            List<Booking> owner2Bookings = bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(owner2Id, now, now, pageable);
            assertNotNull(owner2Bookings);
            assertEquals(0, owner2Bookings.size());
        }

        @Test
        @DisplayName("State.Future - byOwnerId")
        public void test_T1080_PS01_findByItemOwnerIdAndStartGreaterThanEqualOrderByStartDesc() {
            LocalDateTime now = LocalDateTime.now();

            List<Booking> owner1Bookings = bookingRepository.findByItemOwnerIdAndStartGreaterThanEqualOrderByStartDesc(owner1Id, now, pageable);
            assertNotNull(owner1Bookings);
            assertEquals(3, owner1Bookings.size());

            List<Booking> owner2Bookings = bookingRepository.findByItemOwnerIdAndStartGreaterThanEqualOrderByStartDesc(owner2Id, now, pageable);
            assertNotNull(owner2Bookings);
            assertEquals(1, owner2Bookings.size());
        }

        @Test
        @DisplayName("State.Status - byOwnerId")
        public void test_T1090_PS01_findByItemOwnerIdAndStatusOrderByStartDesc() {
            List<Booking> owner1Bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(owner1Id, Status.APPROVED, pageable);
            assertNotNull(owner1Bookings);
            assertEquals(4, owner1Bookings.size());

            owner1Bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(owner1Id, Status.REJECTED, pageable);
            assertNotNull(owner1Bookings);
            assertEquals(1, owner1Bookings.size());

            List<Booking> owner2Bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(owner2Id, Status.APPROVED, pageable);
            assertNotNull(owner2Bookings);
            assertEquals(2, owner2Bookings.size());

            owner2Bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(owner2Id, Status.REJECTED, pageable);
            assertNotNull(owner2Bookings);
            assertEquals(0, owner2Bookings.size());
        }

        @Test
        @DisplayName("State.All - byOwnerId")
        public void test_T1100_PS01_findByItemOwnerIdOrderByStartDesc() {
            List<Booking> owner1Bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(owner1Id, pageable);
            assertNotNull(owner1Bookings);
            assertEquals(5, owner1Bookings.size());

            List<Booking> owner2Bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(owner2Id, pageable);
            assertNotNull(owner2Bookings);
            assertEquals(2, owner2Bookings.size());
        }
    }
}