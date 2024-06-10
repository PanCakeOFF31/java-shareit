package ru.practicum.shareit.common.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.UnsupportedStateException;
import ru.practicum.shareit.common.error.ErrorResponse;

@RestControllerAdvice(basePackages = "ru.practicum.shareit")
@Slf4j
public class CommonControllerAdvice {
    private final String className = this.getClass().getName();

    @ExceptionHandler(UnsupportedStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedStateException(final UnsupportedStateException exception) {
        log.debug(className + "- handleUnsupportedStateException");

        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS",
                "Допустимые значения: ALL, PAST, FUTURE, CURRENT, WAITING, REJECTED",
                exception.getMessage());
    }

}