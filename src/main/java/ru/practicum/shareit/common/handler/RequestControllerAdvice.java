package ru.practicum.shareit.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.common.error.ErrorResponse;

@RestControllerAdvice(basePackages = "ru.practicum.shareit")
@Slf4j
public class RequestControllerAdvice {
    private final String className = this.getClass().getName();

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.debug(className + "- - handleMethodArgumentNotValidException()");

        return new ErrorResponse("Ошибка валидации передаваемой сущности в теле запроса.",
                "Поле/поля или значение поля/полей не соответствуют указанным ограничениям",
                exception.getMessage());
    }


    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException exception) {
        log.debug(className + "- - handleMissingRequestHeaderException()");

        return new ErrorResponse("Ошибка валидации заголовка запроса.",
                "Пропущена часть обязательных заголовков",
                exception.getMessage());
    }
}
