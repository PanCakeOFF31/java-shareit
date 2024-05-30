package ru.practicum.shareit.request.exception;

import ru.practicum.shareit.common.exception.NotFoundException;

public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException() {
        super();
    }

    public RequestNotFoundException(String message) {
        super(message);
    }
}
