package ru.practicum.shareit.user.exception;

public class SameUserException extends RuntimeException {
    public SameUserException() {
        super();
    }

    public SameUserException(String message) {
        super(message);
    }
}
