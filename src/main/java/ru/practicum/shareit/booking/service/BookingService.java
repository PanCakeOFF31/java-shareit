package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    Optional<Booking> findBookingByIdFetch(final long bookingId);

    Booking getBookingByIdFetch(final long bookingId);

    Optional<Booking> findBookingById(final long bookingId);

    Booking getBookingById(final long bookingId);

    BookingResponseDto createBooking(final BookingRequestDto bookingDto, final long userId);

    BookingResponseDto toBook(final long ownerId, final long bookingId, final boolean approved);

    Booking getBookingByIdAndOwnerIdOrBookerId(final long bookerOrOwnerId, final long bookingId);

    BookingResponseDto getBookingDto(final long bookerOrOwnerId, final long bookingId);

    Optional<Booking> findByIdAndBookerIdOrOwnerId(final long bookerOrOwnerId, final long bookingId);

    List<BookingResponseDto> getAllBookingByBooker(final long userId, final String state);

    List<BookingResponseDto> getAllBookingByOwner(final long ownerId, final String state);

    Collection<BookingResponseDto> getAll();

    boolean containsBookingById(final long bookingId);

    void bookingExists(final long bookingId);

}
