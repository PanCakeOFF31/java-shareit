package ru.practicum.shareit.booking.exception;

public class BookingItemUnavailableException extends RuntimeException {
    public BookingItemUnavailableException() {
        super();
    }

    public BookingItemUnavailableException(String message) {
        super(message);
    }
}
