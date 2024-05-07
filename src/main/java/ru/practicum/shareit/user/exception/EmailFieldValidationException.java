package ru.practicum.shareit.user.exception;

public class EmailFieldValidationException extends RuntimeException {
    public EmailFieldValidationException() {
        super();
    }

    public EmailFieldValidationException(String message) {
        super(message);
    }
}
