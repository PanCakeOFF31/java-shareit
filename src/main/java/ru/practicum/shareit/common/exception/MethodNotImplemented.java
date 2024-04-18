package ru.practicum.shareit.common.exception;

public class MethodNotImplemented extends RuntimeException {
    public MethodNotImplemented() {
    }

    public MethodNotImplemented(String message) {
        super(message);
    }
}
