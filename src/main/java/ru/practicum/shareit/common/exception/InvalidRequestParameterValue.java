package ru.practicum.shareit.common.exception;

public class InvalidRequestParameterValue extends RuntimeException {
    public InvalidRequestParameterValue() {
        super();
    }

    public InvalidRequestParameterValue(String message) {
        super(message);
    }
}
