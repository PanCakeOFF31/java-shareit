package ru.practicum.shareit.booking.exception;

public class UnsupportedStateException extends RuntimeException {
    public UnsupportedStateException() {
        super();
    }

    public UnsupportedStateException(String message) {
        super(message);
    }
}
