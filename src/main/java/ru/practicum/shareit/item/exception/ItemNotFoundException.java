package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.common.exception.NotFoundException;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException() {
        super();
    }

    public ItemNotFoundException(String message) {
        super(message);
    }
}
