package ru.practicum.shareit.user.exception;

public class UserFieldValidationException extends RuntimeException {
    public UserFieldValidationException(String message) {
        super(message);
    }
}
