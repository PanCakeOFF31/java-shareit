package ru.practicum.shareit.booking.exception;

public class SameBookerAndOwnerException extends RuntimeException {

    public SameBookerAndOwnerException(String message) {
        super(message);
    }
}
