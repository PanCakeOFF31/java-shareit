package ru.practicum.shareit.item.exception;

public class ItemFieldValidationException extends RuntimeException {
    public ItemFieldValidationException() {
        super();
    }

    public ItemFieldValidationException(String message) {
        super(message);
    }
}
