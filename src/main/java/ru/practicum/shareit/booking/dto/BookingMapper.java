package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public Booking toBooking(final BookingDto bookingDto) {
        return Booking.builder()
                .build();
    }

    public BookingDto toBookingDto(final Booking bookingDto) {
        return BookingDto.builder()
                .build();
    }
}
