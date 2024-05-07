package ru.practicum.shareit.booking.exception;

public class BookingFieldValidationException extends RuntimeException {
    public BookingFieldValidationException() {
        super();
    }

    public BookingFieldValidationException(String message) {
        super(message);
    }
}
