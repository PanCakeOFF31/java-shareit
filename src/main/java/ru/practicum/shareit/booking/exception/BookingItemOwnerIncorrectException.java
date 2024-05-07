package ru.practicum.shareit.booking.exception;

public class BookingItemOwnerIncorrectException extends RuntimeException {
    public BookingItemOwnerIncorrectException() {
        super();
    }

    public BookingItemOwnerIncorrectException(String message) {
        super(message);
    }
}
