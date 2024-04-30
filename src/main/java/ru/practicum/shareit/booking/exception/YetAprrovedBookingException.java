package ru.practicum.shareit.booking.exception;

public class YetAprrovedBookingException extends RuntimeException {
    public YetAprrovedBookingException() {
        super();
    }

    public YetAprrovedBookingException(String message) {
        super(message);
    }
}
