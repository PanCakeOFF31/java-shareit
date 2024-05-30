package ru.practicum.shareit.user.exception;

public class EmailFieldValidationException extends RuntimeException {
    public EmailFieldValidationException(String message) {
        super(message);
    }
}
