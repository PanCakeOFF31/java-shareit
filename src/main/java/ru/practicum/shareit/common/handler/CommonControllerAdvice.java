package ru.practicum.shareit.common.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.common.error.ErrorResponse;
import ru.practicum.shareit.common.exception.MethodNotImplemented;
import ru.practicum.shareit.item.exception.ItemFieldValidationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnerIncorrectException;
import ru.practicum.shareit.user.exception.SameUserEmailException;
import ru.practicum.shareit.user.exception.UserFieldValidationException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@RestControllerAdvice(basePackages = "ru.practicum.shareit")
@Slf4j
public class CommonControllerAdvice {
    private final String className = this.getClass().getName();

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRunTimeException(final RuntimeException exception) {
        log.info(className + "- handleRunTimeException()");
        log.warn(exception.getClass().toString());

        return new ErrorResponse("RuntimeException",
                "Не предвиденная ошибка, которую не предвидели.",
                exception.getClass().toString());
    }

    @ExceptionHandler(MethodNotImplemented.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodNotImplemented(final MethodNotImplemented exception) {
        log.info(className + "handleMethodNotImplemented");

        return new ErrorResponse("Ошибка выполнения запроса",
                "Проблемы реализацией endpoint, ", exception.getMessage());
    }


    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException exception) {
        log.info(className + "UserNotFoundException");

        return new ErrorResponse("Ошибка существования пользователя",
                "Пользователь с указанным идентификатором отсутствует",
                exception.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(final ItemNotFoundException exception) {
        log.info(className + "handleItemNotFoundException");
        return new ErrorResponse("Ошибка существования предмета",
                "Предмет с указанным идентификатором отсутствует",
                exception.getMessage());
    }

    @ExceptionHandler(SameUserEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleSameUserEmailException(final SameUserEmailException exception) {
        log.info(className + "handleSameUserEmailException");

        return new ErrorResponse("Ошибка дублирования пользователя",
                "Пользователь с указанным email уже существует",
                exception.getMessage());
    }

    @ExceptionHandler(ItemOwnerIncorrectException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleItemOwnerIncorrectException(final ItemOwnerIncorrectException exception) {
        log.info(className + "handleItemOwnerIncorrectException");

        return new ErrorResponse("Ошибка владельца предмета",
                "Предмет с указанным владельцем отсутствует",
                exception.getMessage());
    }

    @ExceptionHandler(UserFieldValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserFieldValidationException(final UserFieldValidationException exception) {
        log.info(className + "handleUserFieldValidationException");

        return new ErrorResponse("Ошибка валидация полей пользователя",
                "В JSON объекте отсутствуют необходимые поля",
                exception.getMessage());
    }

    @ExceptionHandler(ItemFieldValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemFieldValidationException(final ItemFieldValidationException exception) {
        log.info(className + "handleItemFieldValidationException");

        return new ErrorResponse("Ошибка валидация полей предмета",
                "В JSON объекте отсутствуют необходимые поля",
                exception.getMessage());
    }
}