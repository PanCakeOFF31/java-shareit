package ru.practicum.shareit.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.common.error.ErrorResponse;
import ru.practicum.shareit.common.exception.InvalidPaginationSizeException;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.shareit")
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(final HttpMessageNotReadableException exception) {
        log.debug(className + "- - handleHttpMessageNotReadableException()");

        return new ErrorResponse("Ошибка валидации передаваемой сущности в теле запроса.",
                "Пустой или некорректная JSON строка",
                exception.getMessage());
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception) {
        log.debug(className + "- - handleMethodArgumentTypeMismatchException()");

        return new ErrorResponse("Ошибка аргументов запроса: параметры запроса или переменные пути.",
                "Пропущены обязательные аргументы",
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

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException exception) {
        log.debug("{}- - handleMissingServletRequestParameterException()", className);

        return new ErrorResponse("Ошибка валидации параметра запроса.",
                "Пропущена часть обязательных параметров запроса",
                exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException exception) {
        log.debug("{}- - handleHttpRequestMethodNotSupportedException()", className);

        return new ErrorResponse("Ошибка валидации endpoint.",
                "Метод не поддерживается для такого пути запроса",
                exception.getMessage());
    }

    @ExceptionHandler(InvalidPaginationSizeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidPaginationSizeException(final InvalidPaginationSizeException exception) {
        log.debug("{}- - handleInvalidPaginationSizeException()", className);

        return new ErrorResponse("Ошибка допустимых значений выборки.",
                "limit - положительное число или ноль, а size - положительное число",
                exception.getMessage());
    }
}
