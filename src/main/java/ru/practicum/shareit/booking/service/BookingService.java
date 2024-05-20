package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    Optional<Booking> findBookingById(final long bookingId);

    Booking getBookingById(final long bookingId);

    BookingResponseDto createBooking(final BookingRequestDto bookingDto, final long bookerId);

    BookingResponseDto toBook(final long ownerId, final long bookingId, final boolean approved);

    Booking getBookingByIdAndOwnerIdOrBookerId(final long bookingId, final long bookerOrOwnerId);

    BookingResponseDto getBookingDto(final long bookingId, final long bookerOrOwnerId);

    Optional<Booking> findByIdAndBookerIdOrOwnerId(final long bookerOrOwnerId, final long bookingId);

    List<BookingResponseDto> getAllBookingByBooker(final long userId,
                                                   final String state,
                                                   final int from,
                                                   final int size);

    List<BookingResponseDto> getAllBookingByOwner(final long ownerId,
                                                  final String state,
                                                  final int from,
                                                  final int size);
}
