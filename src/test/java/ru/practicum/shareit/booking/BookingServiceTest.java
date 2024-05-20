package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.common.exception.InvalidPaginationSizeException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static ru.practicum.shareit.booking.model.State.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;

    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;
    private Booking expectedBooking;
    private User expectedOwner;
    private Item expectedItem;
    private User expectedBooker;

    private long anyBookingId;
    private long anyOwnerId;
    private long anyItemId;
    private long anyBookerId;

    private LocalDateTime ldt;
    private int from;
    private int size;
    private Pageable pageable;

    @BeforeEach
    public void preTestInitialization() {
        anyBookingId = 888L;
        anyOwnerId = 5L;
        anyItemId = 2L;
        anyBookerId = 77L;

        ldt = LocalDateTime.now();

        from = 0;
        size = 10;
        pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        expectedOwner = User.builder()
                .id(anyOwnerId)
                .name("maxim")
                .email("mak@yandex.ru")
                .build();

        expectedItem = Item.builder()
                .id(anyItemId)
                .name("item-name")
                .description("any-description")
                .available(true)
                .owner(expectedOwner)
                .comments(new ArrayList<>())
                .request(null)
                .build();

        expectedBooker = User.builder()
                .id(anyBookerId)
                .name("micle")
                .email("miclek@yandex.ru")
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(expectedItem.getId())
                .start(ldt.plusHours(1))
                .end(ldt.plusHours(5))
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(anyBookingId)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(ItemMapper.mapToItemBookingDto(expectedItem))
                .booker(UserMapper.mapToUserBookingDto(expectedBooker))
                .status(Status.WAITING)
                .build();

        expectedBooking = Booking.builder()
                .id(anyBookingId)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(expectedItem)
                .booker(expectedBooker)
                .status(Status.WAITING)
                .build();
    }


    @Test
    public void test_T0010_PS01_createBooking() {
        Mockito.when(itemService.getItemById(anyItemId))
                .thenReturn(expectedItem);

        Mockito.when(userService.getUserById(anyBookerId))
                .thenReturn(expectedBooker);

        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenReturn(expectedBooking);

        BookingResponseDto createdBooking = bookingService.createBooking(bookingRequestDto, anyBookerId);

        assertEquals(createdBooking, bookingResponseDto);

        Mockito.verify(itemService, Mockito.only()).getItemById(anyItemId);
        Mockito.verify(userService, Mockito.only()).getUserById(anyBookerId);
        Mockito.verify(bookingRepository, Mockito.only()).save(any(Booking.class));

        Mockito.verifyNoMoreInteractions(itemService);
        Mockito.verifyNoMoreInteractions(userService);
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void test_T0010_NS01_createBooking_invalidStartEnd() {
        BookingRequestDto invalidBookingRequest = bookingRequestDto.toBuilder().start(ldt.plusDays(10)).build();

        assertThrows(BookingFieldValidationException.class, () -> bookingService.createBooking(invalidBookingRequest, anyBookerId));

        Mockito.verifyNoInteractions(itemService);
        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(userService);
    }


    @Test
    public void test_T0010_NS02_createBooking_noItemWithId() {
        Mockito.when(itemService.getItemById(anyItemId))
                .thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(bookingRequestDto, anyBookerId));

        Mockito.verify(itemService, Mockito.only()).getItemById(anyItemId);
        Mockito.verifyNoMoreInteractions(itemService);

        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(userService);
    }

    @Test
    public void test_T0010_NS03_createBooking_unavailableItem() {
        Item invaliItem = new Item(expectedItem);
        invaliItem.setAvailable(false);

        Mockito.when(itemService.getItemById(anyItemId))
                .thenReturn(invaliItem);

        assertThrows(BookingItemUnavailableException.class, () -> bookingService.createBooking(bookingRequestDto, anyBookerId));


        Mockito.verify(itemService, Mockito.only()).getItemById(anyItemId);
        Mockito.verifyNoMoreInteractions(itemService);

        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(bookingRepository);
    }


    @Test
    public void test_T0010_NS04_createBooking_bookerIsOwner() {
        Mockito.when(itemService.getItemById(anyItemId))
                .thenReturn(expectedItem);

        assertThrows(SameBookerAndOwnerException.class, () -> bookingService.createBooking(bookingRequestDto, anyOwnerId));

        Mockito.verify(itemService, Mockito.only()).getItemById(anyItemId);
        Mockito.verifyNoMoreInteractions(itemService);

        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0010_NS05_createBooking_noUserWithId() {
        Mockito.when(itemService.getItemById(anyItemId))
                .thenReturn(expectedItem);

        Mockito.when(userService.getUserById(anyBookerId))
                .thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(bookingRequestDto, anyBookerId));

        Mockito.verify(itemService, Mockito.only()).getItemById(anyItemId);
        Mockito.verify(userService, Mockito.only()).getUserById(anyBookerId);

        Mockito.verifyNoMoreInteractions(itemService);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0020_PS01_toBook() {
        Mockito.doNothing().when(userService).userExists(anyOwnerId);

        Mockito.when(bookingRepository.findById(anyBookingId))
                .thenReturn(Optional.of(expectedBooking));

        Mockito.doNothing().when(itemService).ownerOwnsItem(anyItemId, anyOwnerId);

        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenReturn(expectedBooking);

        BookingResponseDto approvedBooking = bookingService.toBook(anyOwnerId, anyBookingId, true);
        BookingResponseDto expectedApprovedBooking = bookingResponseDto.toBuilder().status(Status.APPROVED).build();

        assertEquals(approvedBooking, expectedApprovedBooking);

        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(anyBookingId);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(any(Booking.class));
        Mockito.verifyNoMoreInteractions(bookingRepository);

        Mockito.verify(itemService, Mockito.only()).ownerOwnsItem(anyItemId, anyOwnerId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void test_T0020_NS01_toBook_noUserWithId() {
        Mockito.doThrow(UserNotFoundException.class).when(userService).userExists(anyOwnerId);

        assertThrows(UserNotFoundException.class, () -> bookingService.toBook(anyOwnerId, anyBookingId, true));

        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    public void test_T0020_NS02_toBook_noBookingById() {
        Mockito.doNothing().when(userService).userExists(anyOwnerId);

        Mockito.when(bookingRepository.findById(anyBookingId))
                .thenThrow(BookingByIdAndOwnerIdNotFoundException.class);

        assertThrows(BookingByIdAndOwnerIdNotFoundException.class, () -> bookingService.toBook(anyOwnerId, anyBookingId, true));

        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(anyBookingId);
        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
        Mockito.verifyNoMoreInteractions(bookingRepository);

        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    public void test_T0020_NS03_toBook_ownerOwnsItem() {
        Mockito.doNothing().when(userService).userExists(anyOwnerId);

        Mockito.when(bookingRepository.findById(anyBookingId))
                .thenReturn(Optional.of(expectedBooking));

        Mockito.doThrow(BookingItemOwnerIncorrectException.class).when(itemService).ownerOwnsItem(anyItemId, anyOwnerId);

        assertThrows(BookingItemOwnerIncorrectException.class, () -> bookingService.toBook(anyOwnerId, anyBookingId, true));

        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(itemService, Mockito.only()).ownerOwnsItem(anyItemId, anyOwnerId);
        Mockito.verifyNoMoreInteractions(itemService);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(anyBookingId);
        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void test_T0020_NS04_toBook_yetApproved() {
        Booking invalidBooking = new Booking(expectedBooking);
        invalidBooking.setStatus(Status.APPROVED);

        Mockito.doNothing().when(userService).userExists(anyOwnerId);

        Mockito.when(bookingRepository.findById(anyBookingId))
                .thenReturn(Optional.of(invalidBooking));

        Mockito.doNothing().when(itemService).ownerOwnsItem(anyItemId, anyOwnerId);

        assertThrows(YetAprrovedBookingException.class, () -> bookingService.toBook(anyOwnerId, anyBookingId, true));

        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(itemService, Mockito.only()).ownerOwnsItem(anyItemId, anyOwnerId);
        Mockito.verifyNoMoreInteractions(itemService);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(anyBookingId);
        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void test_T0030_PS01_getBooking() {
        Mockito.doNothing().when(userService).userExists(anyBookerId);

        Mockito.when(bookingRepository.findByIdAndBookerIdOrOwnerId(anyBookingId, anyBookerId))
                .thenReturn(Optional.of(expectedBooking));

        BookingResponseDto gotBooking = bookingService.getBookingDto(anyBookingId, anyBookerId);

        assertEquals(gotBooking, bookingResponseDto);

        Mockito.verify(userService, Mockito.only()).userExists(anyBookerId);
        Mockito.verify(bookingRepository, Mockito.only()).findByIdAndBookerIdOrOwnerId(anyBookingId, anyBookerId);
        Mockito.verifyNoInteractions(itemService);

        Mockito.verifyNoMoreInteractions(userService);
        Mockito.verifyNoMoreInteractions(bookingRepository);

    }

    @Test
    public void test_T0030_NS01_getBooking_noUserExist() {
        Mockito.doThrow(UserNotFoundException.class).when(userService).userExists(anyBookerId);

        assertThrows(UserNotFoundException.class, () -> bookingService.getBookingDto(anyBookingId, anyBookerId));

        Mockito.verify(userService, Mockito.only()).userExists(anyBookerId);
        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(itemService);

        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_T0030_NS02_getBooking_noBookerOrOwner() {
        Mockito.doNothing().when(userService).userExists(anyBookerId);
        Mockito.doThrow(BookingByIdAndOwnerIdNotFoundException.class).when(bookingRepository).findByIdAndBookerIdOrOwnerId(anyBookingId, anyBookerId);

        assertThrows(BookingByIdAndOwnerIdNotFoundException.class, () -> bookingService.getBookingDto(anyBookingId, anyBookerId));

        Mockito.verify(userService, Mockito.only()).userExists(anyBookerId);
        Mockito.verify(bookingRepository, Mockito.only()).findByIdAndBookerIdOrOwnerId(anyBookingId, anyBookerId);
        Mockito.verifyNoInteractions(itemService);

        Mockito.verifyNoMoreInteractions(userService);
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    private static Stream<Arguments> giveArgsFor_T0040_PS01() {
        return Stream.of(
                arguments(PAST),
                arguments(CURRENT),
                arguments(FUTURE),
                arguments(WAITING),
                arguments(REJECTED),
                arguments(ALL)
        );
    }

    @ParameterizedTest
    @MethodSource("giveArgsFor_T0040_PS01")
    public void test_T0040_PS01_getAllBookingByBooker(State state) {
        Booking booking1 = expectedBooking.toBuilder().id(11L).build();
        Booking booking2 = expectedBooking.toBuilder().id(20L).build();

        List<Booking> result = List.of(booking1, booking2);
        List<BookingResponseDto> convertedResult = BookingMapper.mapToBookingResponseDto(result);

        Mockito.doNothing().when(userService).userExists(anyOwnerId);

        switch (state) {
            case PAST:
                Mockito.when(bookingRepository
                                .findByBookerIdAndEndLessThanOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(result);
                break;
            case CURRENT:
                Mockito.when(bookingRepository
                                .findByBookerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(result);
                break;
            case FUTURE:
                Mockito.when(bookingRepository
                                .findByBookerIdAndStartGreaterThanEqualOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(result);
                break;
            case WAITING:
                Mockito.when(bookingRepository
                                .findByBookerIdAndStatusOrderByStartDesc(anyOwnerId, Status.WAITING, pageable))
                        .thenReturn(result);
                break;
            case REJECTED:
                Mockito.when(bookingRepository
                                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(Pageable.class)))
                        .thenReturn(result);
                break;
            default:
                Mockito.when(bookingRepository
                                .findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                        .thenReturn(result);
        }

        List<BookingResponseDto> gotBookingList = bookingService.getAllBookingByBooker(anyOwnerId, state.toString(), from, size);

        assertEquals(gotBookingList.size(), convertedResult.size());
        assertEquals(gotBookingList.get(0), convertedResult.get(0));

        switch (state) {
            case PAST:
                Mockito.verify(bookingRepository, Mockito.only()).findByBookerIdAndEndLessThanOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
                break;
            case CURRENT:
                Mockito.verify(bookingRepository, Mockito.only()).findByBookerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
                break;
            case FUTURE:
                Mockito.verify(bookingRepository, Mockito.only()).findByBookerIdAndStartGreaterThanEqualOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
                break;
            case WAITING:
                Mockito.verify(bookingRepository, Mockito.only()).findByBookerIdAndStatusOrderByStartDesc(anyOwnerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                Mockito.verify(bookingRepository, Mockito.only()).findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(Pageable.class));
                break;
            default:
                Mockito.verify(bookingRepository, Mockito.only()).findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class));
        }

        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    public void test_T0040_NS01_getAllBookingByBooker_noUserWithId() {
        Mockito.doThrow(UserNotFoundException.class).when(userService).userExists(anyOwnerId);

        assertThrows(UserNotFoundException.class, () -> bookingService.getAllBookingByBooker(anyOwnerId, "ALL", from, size));

        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    public void test_T0040_NS02_getAllBookingByBooker_invalidPagination() {
        assertThrows(InvalidPaginationSizeException.class, () -> bookingService.getAllBookingByBooker(anyOwnerId, "ALL", -5, -10));

        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    public void test_T0040_NS03_getAllBookingByBooker_invalidState() {
        Mockito.doNothing().when(userService).userExists(anyOwnerId);

        assertThrows(UnsupportedStateException.class, () -> bookingService.getAllBookingByBooker(anyOwnerId, "asd-0123", from, size));

        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);
        Mockito.verifyNoInteractions(itemService);
    }

    private static Stream<Arguments> giveArgsFor_T0050_PS01() {
        return Stream.of(
                arguments(PAST),
                arguments(CURRENT),
                arguments(FUTURE),
                arguments(WAITING),
                arguments(REJECTED),
                arguments(ALL)
        );
    }

    @ParameterizedTest
    @MethodSource("giveArgsFor_T0050_PS01")
    public void test_T0050_PS01_getAllBookingByBooker(State state) {
        Booking booking1 = expectedBooking.toBuilder().id(11L).build();
        Booking booking2 = expectedBooking.toBuilder().id(20L).build();

        List<Booking> result = List.of(booking1, booking2);
        List<BookingResponseDto> convertedResult = BookingMapper.mapToBookingResponseDto(result);

        Mockito.doNothing().when(userService).userExists(anyOwnerId);

        switch (state) {
            case PAST:
                Mockito.when(bookingRepository
                                .findByItemOwnerIdAndEndLessThanOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(result);
                break;
            case CURRENT:
                Mockito.when(bookingRepository
                                .findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(result);
                break;
            case FUTURE:
                Mockito.when(bookingRepository
                                .findByItemOwnerIdAndStartGreaterThanEqualOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                        .thenReturn(result);
                break;
            case WAITING:
                Mockito.when(bookingRepository
                                .findByItemOwnerIdAndStatusOrderByStartDesc(anyOwnerId, Status.WAITING, pageable))
                        .thenReturn(result);
                break;
            case REJECTED:
                Mockito.when(bookingRepository
                                .findByItemOwnerIdAndStatusOrderByStartDesc(anyOwnerId, Status.REJECTED, pageable))
                        .thenReturn(result);
                break;
            default:
                Mockito.when(bookingRepository
                                .findByItemOwnerIdOrderByStartDesc(anyOwnerId, pageable))
                        .thenReturn(result);
        }

        List<BookingResponseDto> gotBookingList = bookingService.getAllBookingByOwner(anyOwnerId, state.toString(), from, size);

        assertEquals(gotBookingList.size(), convertedResult.size());
        assertEquals(gotBookingList.get(0), convertedResult.get(0));

        switch (state) {
            case PAST:
                Mockito.verify(bookingRepository, Mockito.only()).findByItemOwnerIdAndEndLessThanOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
                break;
            case CURRENT:
                Mockito.verify(bookingRepository, Mockito.only()).findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
                break;
            case FUTURE:
                Mockito.verify(bookingRepository, Mockito.only()).findByItemOwnerIdAndStartGreaterThanEqualOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
                break;
            case WAITING:
                Mockito.verify(bookingRepository, Mockito.only()).findByItemOwnerIdAndStatusOrderByStartDesc(anyOwnerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                Mockito.verify(bookingRepository, Mockito.only()).findByItemOwnerIdAndStatusOrderByStartDesc(anyOwnerId, Status.REJECTED, pageable);
                break;
            default:
                Mockito.verify(bookingRepository, Mockito.only()).findByItemOwnerIdOrderByStartDesc(anyOwnerId, pageable);
        }

        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    public void test_T0050_NS01_getAllBookingByBooker_noUserWithId() {
        Mockito.doThrow(UserNotFoundException.class).when(userService).userExists(anyOwnerId);

        assertThrows(UserNotFoundException.class, () -> bookingService.getAllBookingByOwner(anyOwnerId, "ALL", from, size));

        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    public void test_T0050_NS02_getAllBookingByBooker_invalidPagination() {
        assertThrows(InvalidPaginationSizeException.class, () -> bookingService.getAllBookingByOwner(anyOwnerId, "ALL", -5, -10));

        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    public void test_T0050_NS03_getAllBookingByBooker_invalidState() {
        Mockito.doNothing().when(userService).userExists(anyOwnerId);

        assertThrows(UnsupportedStateException.class, () -> bookingService.getAllBookingByOwner(anyOwnerId, "asd-0123", from, size));

        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);
        Mockito.verifyNoInteractions(itemService);
    }
}