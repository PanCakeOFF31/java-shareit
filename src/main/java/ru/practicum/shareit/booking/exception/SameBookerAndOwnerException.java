package ru.practicum.shareit.booking.exception;

public class SameBookerAndOwnerException extends RuntimeException {
    public SameBookerAndOwnerException() {
        super();
    }

    public SameBookerAndOwnerException(String message) {
        super(message);
    }
}
