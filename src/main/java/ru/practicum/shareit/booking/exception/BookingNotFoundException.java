package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.common.exception.NotFoundException;

public class BookingNotFoundException extends NotFoundException {
    public BookingNotFoundException() {
        super();
    }

    public BookingNotFoundException(String message) {
        super(message);
    }
}
