package ru.practicum.shareit.common.handler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.common.error.ErrorResponse;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnerIncorrectException;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.user.exception.EmailFieldValidationException;
import ru.practicum.shareit.user.exception.SameUserEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonControllerAdviceTest {
    private static CommonControllerAdvice commonControllerAdvice;

    @BeforeAll
    public static void initialize() {
        commonControllerAdvice = new CommonControllerAdvice();
    }

    @Test
    public void test_T0010_PS01_UserNotFoundException() {
        ErrorResponse response = commonControllerAdvice.handleUserNotFoundException(new UserNotFoundException(""));
        assertEquals("Ошибка существования пользователя", response.getError());

    }

    @Test
    public void test_T0020_PS01_ItemNotFoundException() {
        ErrorResponse response = commonControllerAdvice.handleItemNotFoundException(new ItemNotFoundException(""));
        assertEquals("Ошибка существования предмета", response.getError());

    }

    @Test
    public void test_T0030_PS01_BookingByIdAndOwnerIdNotFoundException() {
        ErrorResponse response = commonControllerAdvice.handleBookingByIdAndOwnerIdNotFoundException(new BookingByIdAndOwnerIdNotFoundException(""));
        assertEquals("Ошибка существования бронирования", response.getError());

    }

    @Test
    public void test_T0040_PS01_RequestNotFoundException() {
        ErrorResponse response = commonControllerAdvice.handleRequestNotFoundException(new RequestNotFoundException(""));
        assertEquals("Ошибка существования запроса", response.getError());

    }

    @Test
    public void test_T0050_PS01_SameUserEmailException() {
        ErrorResponse response = commonControllerAdvice.handleSameUserEmailException(new SameUserEmailException(""));
        assertEquals("Ошибка дублирования пользователя", response.getError());

    }

    @Test
    public void test_T0060_PS01_ItemOwnerIncorrectException() {
        ErrorResponse response = commonControllerAdvice.handleItemOwnerIncorrectException(new ItemOwnerIncorrectException(""));
        assertEquals("Ошибка владельца предмета", response.getError());

    }

    @Test
    public void test_T0070_PS01_BookingItemOwnerIncorrectException() {
        ErrorResponse response = commonControllerAdvice.handleBookingItemOwnerIncorrectException(new BookingItemOwnerIncorrectException(""));
        assertEquals("Ошибка владельца предмета", response.getError());

    }

    @Test
    public void test_T0080_PS01_EmailFieldValidationException() {
        ErrorResponse response = commonControllerAdvice.handleEmailFieldValidationException(new EmailFieldValidationException(""));
        assertEquals("Ошибка валидация почты пользователя", response.getError());

    }

    @Test
    public void test_T0090_PS01_InvalidDataAccessResourceUsageException() {
        ErrorResponse response = commonControllerAdvice.handleInvalidDataAccessResourceUsageException(new InvalidDataAccessResourceUsageException(""));
        assertEquals("Ошибка доступа к ресурсам в репозиторий", response.getError());

    }

    @Test
    public void test_T0100_PS01_BookingItemUnavailableException() {
        ErrorResponse response = commonControllerAdvice.handleBookingItemUnavailableException(new BookingItemUnavailableException(""));
        assertEquals("Ошибка бронирования недоступной вещи", response.getError());

    }

    @Test
    public void test_T0110_PS01_SameBookerAndOwnerException() {
        ErrorResponse response = commonControllerAdvice.handleSameBookerAndOwnerException(new SameBookerAndOwnerException(""));
        assertEquals("Ошибка бронирования вещи", response.getError());

    }


    @Test
    public void test_T0130_PS01_YetAprrovedBookingException() {
        ErrorResponse response = commonControllerAdvice.handleYetAprrovedBookingException(new YetAprrovedBookingException(""));
        assertEquals("Запись уже подтверждена", response.getError());

    }

    @Test
    public void test_T0140_PS01_UserNotBookedItemException() {
        ErrorResponse response = commonControllerAdvice.handleUserNotBookedItemException(new UserNotBookedItemException());
        assertEquals("Проблема с создание комментария", response.getError());

    }

    @Test
    public void test_T0150_PS01_DataIntegrityViolationException() {
        ErrorResponse response = commonControllerAdvice.handleDataIntegrityViolationException(new DataIntegrityViolationException(""));
        assertEquals("Конфликт с интеграцией данных в хранилище", response.getError());

    }

    @Test
    public void test_T0160_PS01_BookingFieldValidationException() {
        ErrorResponse response = commonControllerAdvice.handleBookingFieldValidationException(new BookingFieldValidationException(""));
        assertEquals("Ошибка валидация полей предмета", response.getError());
    }
}