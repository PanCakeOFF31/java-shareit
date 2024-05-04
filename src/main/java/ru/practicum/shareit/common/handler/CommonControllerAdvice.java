package ru.practicum.shareit.common.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.*;
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
        log.debug(className + "- - handleRunTimeException()");
        log.warn(exception.getClass().toString());

        return new ErrorResponse("RuntimeException",
                "Не предвиденная ошибка, которую не предвидели.",
                exception.getClass().toString());
    }

    @ExceptionHandler(MethodNotImplemented.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodNotImplemented(final MethodNotImplemented exception) {
        log.debug(className + "- handleMethodNotImplemented");

        return new ErrorResponse("Ошибка выполнения запроса",
                "Проблемы реализацией endpoint, ", exception.getMessage());
    }


    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException exception) {
        log.debug(className + "- UserNotFoundException");

        return new ErrorResponse("Ошибка существования пользователя",
                "Пользователь с указанным идентификатором отсутствует",
                exception.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(final ItemNotFoundException exception) {
        log.debug(className + "- handleItemNotFoundException");
        return new ErrorResponse("Ошибка существования предмета",
                "Предмет с указанным идентификатором отсутствует",
                exception.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFoundException(final BookingNotFoundException exception) {
        log.debug(className + "- handleBookingNotFoundException");
        return new ErrorResponse("Ошибка существования бронирования",
                "Бронь с указанным идентификатором отсутствует",
                exception.getMessage());
    }

    @ExceptionHandler(SameUserEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleSameUserEmailException(final SameUserEmailException exception) {
        log.debug(className + "- handleSameUserEmailException");

        return new ErrorResponse("Ошибка дублирования пользователя",
                "Пользователь с указанным email уже существует",
                exception.getMessage());
    }

    @ExceptionHandler(ItemOwnerIncorrectException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleItemOwnerIncorrectException(final ItemOwnerIncorrectException exception) {
        log.debug(className + "- handleItemOwnerIncorrectException");

        return new ErrorResponse("Ошибка владельца предмета",
                "Предмет с указанным владельцем отсутствует",
                exception.getMessage());
    }

    @ExceptionHandler(BookingItemOwnerIncorrectException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingItemOwnerIncorrectException(final BookingItemOwnerIncorrectException exception) {
        log.debug(className + "- handleBookingItemOwnerIncorrectException");

        return new ErrorResponse("Ошибка владельца предмета",
                "Предмет с указанным владельцем отсутствует",
                exception.getMessage());
    }

    @ExceptionHandler(UserFieldValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserFieldValidationException(final UserFieldValidationException exception) {
        log.debug(className + "- handleUserFieldValidationException");

        return new ErrorResponse("Ошибка валидация полей пользователя",
                "В JSON объекте отсутствуют необходимые поля",
                exception.getMessage());
    }

    @ExceptionHandler(ItemFieldValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemFieldValidationException(final ItemFieldValidationException exception) {
        log.debug(className + "- handleItemFieldValidationException");

        return new ErrorResponse("Ошибка валидация полей предмета",
                "В JSON объекте отсутствуют необходимые поля",
                exception.getMessage());
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDataAccessResourceUsageException(final InvalidDataAccessResourceUsageException exception) {
        log.debug(className + "- handleInvalidDataAccessResourceUsageException");

        return new ErrorResponse("Ошибка доступа к ресурсам в репозиторий",
                "Скорее всего какая-то часть запроса не верна.",
                exception.getMessage());
    }

    @ExceptionHandler(BookingItemUnavailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingItemUnavailableException(final BookingItemUnavailableException exception) {
        log.debug(className + "- handleBookingItemUnavailableException");

        return new ErrorResponse("Ошибка бронирования недоступной вещи",
                "Предмет сейчас не доступен",
                exception.getMessage());
    }

    @ExceptionHandler(SameBookerAndOwnerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleSameBookerAndOwnerException(final SameBookerAndOwnerException exception) {
        log.debug(className + "- handleSameBookerAndOwnerException");

        return new ErrorResponse("Ошибка бронирования вещи",
                "Нельзя бронировать вещь, которая принадлежит владельцу",
                exception.getMessage());
    }

    @ExceptionHandler(UnsupportedStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedStateException(final UnsupportedStateException exception) {
        log.debug(className + "- handleUnsupportedStateException");

        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS",
                "Допустимые значения: ALL, PAST, FUTURE, CURRENT, WAITING, REJECTED",
                exception.getMessage());
    }

    @ExceptionHandler(YetAprrovedBookingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleYetAprrovedBookingException(final YetAprrovedBookingException exception) {
        log.debug(className + "- handleYetApprovedBookingException");

        return new ErrorResponse("Запись уже подтверждена",
                "Повторная попытка подтвердить уже подтвержденное бронирование",
                exception.getMessage());
    }

    @ExceptionHandler(UserNotBookedItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotBookedItemException(final UserNotBookedItemException exception) {
        log.debug(className + "- handleUserNotBookedItemException");

        return new ErrorResponse("Проблема с создание комментария",
                "Пользователь не бронировал этот предмет, нельзя писать в таком случае коммент",
                exception.getMessage());
    }

}