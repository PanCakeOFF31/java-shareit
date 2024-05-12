package ru.practicum.shareit.common;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.common.exception.InvalidPaginationSizeException;

@Slf4j
public class CommonValidation {
    public static void paginateValidation(final int from, final int size) {
        if (from < 0 || size < 1) {
            String message = String.format("Недопустимые значения для  from = %d и size = %d", from, size);
            log.warn(message);
            throw new InvalidPaginationSizeException(message);
        }
    }
}
