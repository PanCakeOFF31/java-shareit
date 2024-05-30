package ru.practicum.shareit.common.handler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.exception.UnsupportedStateException;
import ru.practicum.shareit.common.error.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonControllerAdviceTest {
    private static CommonControllerAdvice commonControllerAdvice;

    @BeforeAll
    public static void initialize() {
        commonControllerAdvice = new CommonControllerAdvice();
    }

    @Test
    public void test_T010_PS01_UnsupportedStateException() {
        ErrorResponse response = commonControllerAdvice.handleUnsupportedStateException(new UnsupportedStateException(""));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", response.getError());
    }
}
