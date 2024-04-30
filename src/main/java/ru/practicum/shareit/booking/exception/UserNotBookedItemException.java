package ru.practicum.shareit.booking.exception;

public class UserNotBookedItemException extends RuntimeException {
    public UserNotBookedItemException() {
        super();
    }

    public UserNotBookedItemException(String message) {
        super(message);
    }
}
