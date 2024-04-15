package ru.practicum.shareit.item.exception;

public class ItemOwnerIncorrectException extends RuntimeException {
    public ItemOwnerIncorrectException() {
        super();
    }

    public ItemOwnerIncorrectException(String message) {
        super(message);
    }
}
