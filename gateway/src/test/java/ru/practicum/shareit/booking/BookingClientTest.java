package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.booking.exception.UnsupportedStateException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingClientTest {

    private static BookingClient bookingClient;

    @BeforeAll
    public static void initialize() {
        bookingClient = new BookingClient("http://localhost:9090", new RestTemplateBuilder());
    }

    @Test
    public void test_TEST0010_NS01_getAllBookingByBooker_stateValidation() {
        assertThrows(UnsupportedStateException.class, () -> bookingClient.getAllBookingByBooker(1, "asdkjfsa", 0, 10));
    }

    @Test
    public void test_TEST0020_NS01_getAllBookingByOwner_stateValidation() {
        assertThrows(UnsupportedStateException.class, () -> bookingClient.getAllBookingByOwner(1, "asdkjfsa", 0, 10));
    }
}
