package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.common.exception.NotFoundException;

public class BookingByIdAndOwnerIdNotFoundException extends NotFoundException {
    public BookingByIdAndOwnerIdNotFoundException() {
        super();
    }

    public BookingByIdAndOwnerIdNotFoundException(String message) {
        super(message);
    }
}
