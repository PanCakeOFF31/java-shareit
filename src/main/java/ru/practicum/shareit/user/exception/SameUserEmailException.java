package ru.practicum.shareit.user.exception;

public class SameUserEmailException extends RuntimeException {
    public SameUserEmailException(String message) {
        super(message);
    }
}
