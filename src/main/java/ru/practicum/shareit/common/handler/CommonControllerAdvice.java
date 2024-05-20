package ru.practicum.shareit.common.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.user.exception.EmailFieldValidationException;
import ru.practicum.shareit.user.exception.SameUserEmailException;
import ru.practicum.shareit.user.exception.UserFieldValidationException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@RestControllerAdvice(basePackages = "ru.practicum.shareit")
@Slf4j
public class CommonControllerAdvice {
    private final String className = this.getClass().getName();

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

    @ExceptionHandler(RequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRequestNotFoundException(final RequestNotFoundException exception) {
        log.debug(className + "- handleRequestNotFoundException");
        return new ErrorResponse("Ошибка существования запроса",
                "Запрос с указанным идентификатором отсутствует",
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

    @ExceptionHandler(EmailFieldValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEmailFieldValidationException(final EmailFieldValidationException exception) {
        log.debug(className + "- handleEmailFieldValidationException");

        return new ErrorResponse("Ошибка валидация почты пользователя",
                "В JSON объекте email не соответствует регулярному выражению",
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        log.debug(className + "- handleDataIntegrityViolationException");

        return new ErrorResponse("Конфликт с интеграцией данных в хранилище",
                "Нарушение Data Integrity в БД",
                exception.getMessage());
    }

    @ExceptionHandler(BookingFieldValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingFieldValidationException(final BookingFieldValidationException exception) {
        log.debug(className + "- handleBookingFieldValidationException");

        return new ErrorResponse("Ошибка валидация полей предмета",
                "В JSON объекте отсутствуют необходимые поля",
                exception.getMessage());
    }
}