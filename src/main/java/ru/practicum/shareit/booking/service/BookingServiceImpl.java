package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.CommonValidation;
import ru.practicum.shareit.item.exception.ItemOwnerIncorrectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    private static final String NO_FOUND_BOOKING = "Такого брони с id: %d не существует в хранилище";
    private static final String UNSUPPORTED_STATUS = "Данный статус '%s' не поддерживается";

    private Optional<Booking> findBookingById(long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public Booking getBookingById(long bookingId) {
        return findBookingById(bookingId)
                .orElseThrow(() -> new BookingByIdAndOwnerIdNotFoundException(String.format(NO_FOUND_BOOKING, bookingId)));
    }

    @Transactional
    @Override
    public BookingResponseDto createBooking(final BookingRequestDto bookingRequestDto, final long bookerId) {

        fieldRequestValidation(bookingRequestDto);

        final Item item = itemService.getItemById(bookingRequestDto.getItemId());
        itemToCreateBookValidation(item, bookerId);

        final User user = userService.getUserById(bookerId);

        final Booking booking = BookingMapper.mapToBooking(bookingRequestDto, bookerId);
        booking.setBooker(user);
        booking.setItem(item);

        return BookingMapper.mapToBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto toBook(long ownerId, long bookingId, boolean approved) {

        userService.userExists(ownerId);

        final Booking booking = getBookingById(bookingId);

        try {
            itemService.ownerOwnsItem(booking.getItem().getId(), ownerId);
        } catch (ItemOwnerIncorrectException e) {
//            Дублирую, так как в этом случае Postman тесту нужен не 403, а 404
            String message =
                    String.format("Пользователь c id %d  - не является владельцем вещи id %d", booking.getItem().getId(), ownerId);
            log.warn(message);
            throw new BookingItemOwnerIncorrectException(message);
        }

        statusToBookValidation(booking.getStatus(), approved);

        if (approved)
            booking.setStatus(Status.APPROVED);
        else
            booking.setStatus(Status.REJECTED);

        final Booking approvedBooking = bookingRepository.save(booking);

        return BookingMapper.mapToBookingResponseDto(approvedBooking);
    }

    @Override
    public Optional<Booking> findByIdAndBookerIdOrOwnerId(long bookingId, long bookerOrOwnerId) {
        log.debug("BookingServiceImpl - service.findByIdAndBookerIdOrOwnerId({}, {})", bookingId, bookerOrOwnerId);
        return bookingRepository.findByIdAndBookerIdOrOwnerId(bookingId, bookerOrOwnerId);
    }

    @Override
    public Booking getBookingByIdAndOwnerIdOrBookerId(long bookingId, long bookerOrOwnerId) {
        log.debug("BookingServiceImpl - service.getBooking({}, {})", bookerOrOwnerId, bookingId);
        return findByIdAndBookerIdOrOwnerId(bookingId, bookerOrOwnerId)
                .orElseThrow(() -> new BookingByIdAndOwnerIdNotFoundException(String.format(NO_FOUND_BOOKING, bookingId)));
    }

    @Override
    public BookingResponseDto getBookingDto(long bookingId, long bookerOrOwnerId) {
        log.debug("BookingServiceImpl - service.getBookingDto({}, {})", bookingId, bookerOrOwnerId);

        userService.userExists(bookerOrOwnerId);

        return BookingMapper.mapToBookingResponseDto(getBookingByIdAndOwnerIdOrBookerId(bookingId, bookerOrOwnerId));
    }

    private State stateValidation(final String state) throws UnsupportedStateException {

        try {
            return State.valueOf(state);
        } catch (RuntimeException ignore) {
            String message = String.format(UNSUPPORTED_STATUS, state);
            log.warn(message);
            throw new UnsupportedStateException(message);
        }
    }

    @Override
    public List<BookingResponseDto> getAllBookingByBooker(final long bookerId,
                                                          final String state,
                                                          final int from,
                                                          final int size) {

        CommonValidation.paginateValidation(from, size);
        userService.userExists(bookerId);
        State enumState = stateValidation(state);

        List<Booking> result;
        var now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (enumState) {
            case PAST:
                result = bookingRepository
                        .findByBookerIdAndEndLessThanOrderByStartDesc(bookerId, now, pageable);
                break;
            case CURRENT:
                result = bookingRepository
                        .findByBookerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(bookerId, now, now, pageable);
                break;
            case FUTURE:
                result = bookingRepository
                        .findByBookerIdAndStartGreaterThanEqualOrderByStartDesc(bookerId, now, pageable);
                break;
            case WAITING:
                result = bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED, pageable);
                break;
            default:
                result = bookingRepository
                        .findByBookerIdOrderByStartDesc(bookerId, pageable);
        }

        return BookingMapper.mapToBookingResponseDto(result);
    }

    @Override
    public List<BookingResponseDto> getAllBookingByOwner(long ownerId,
                                                         final String state,
                                                         final int from,
                                                         final int size) {

        CommonValidation.paginateValidation(from, size);
        userService.userExists(ownerId);
        State enumState = stateValidation(state);

        List<Booking> result;
        var now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (enumState) {
            case PAST:
                result = bookingRepository
                        .findByItemOwnerIdAndEndLessThanOrderByStartDesc(ownerId, now, pageable);
                break;
            case CURRENT:
                result = bookingRepository
                        .findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(ownerId, now, now, pageable);
                break;
            case FUTURE:
                result = bookingRepository
                        .findByItemOwnerIdAndStartGreaterThanEqualOrderByStartDesc(ownerId, now, pageable);
                break;
            case WAITING:
                result = bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED, pageable);
                break;
            default:
                result = bookingRepository
                        .findByItemOwnerIdOrderByStartDesc(ownerId, pageable);
        }

        return BookingMapper.mapToBookingResponseDto(result);
    }

    private void itemToCreateBookValidation(final Item item, final long bookerId) {
        log.debug("BookingServiceImpl - service.itemIsAvailable({})", item);

        String message;

        if (!item.getAvailable()) {
            message = "Попытка забронировать недоступную вещь - " + item;
            log.warn(message);
            throw new BookingItemUnavailableException(message);
        }

        if (item.getOwner().getId() == bookerId) {
            message = "Попытка забронировать свою же вещь - " + item;
            log.warn(message);
            throw new SameBookerAndOwnerException(message);
        }
    }

    private void fieldRequestValidation(final BookingRequestDto bookingRequestDto) {
        log.debug("BookingServiceImpl - service.fieldValidation({})", bookingRequestDto);

        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();

        if (end.isBefore(start) || end.equals(start) || start.isBefore(LocalDateTime.now())) {
            String message = "Не верные значения даты для бронирования - " + bookingRequestDto;
            log.warn(message);
            throw new BookingFieldValidationException(message);
        }
    }

    private void statusToBookValidation(final Status status, final boolean approve) {
        if (status.equals(Status.APPROVED) && approve) {
            String message = "Запись уже подтверждена - " + status;
            log.warn(message);
            throw new YetAprrovedBookingException(message);
        }
    }
}
