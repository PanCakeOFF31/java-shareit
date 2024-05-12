package ru.practicum.shareit.request.exception;

public class RequestFieldValidationException extends RuntimeException {
    public RequestFieldValidationException() {
        super();
    }

    public RequestFieldValidationException(String message) {
        super(message);
    }
}
