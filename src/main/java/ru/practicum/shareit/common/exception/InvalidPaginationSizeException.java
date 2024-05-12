package ru.practicum.shareit.common.exception;

public class InvalidPaginationSizeException extends RuntimeException {
    public InvalidPaginationSizeException() {
        super();
    }

    public InvalidPaginationSizeException(String message) {
        super(message);
    }
}
