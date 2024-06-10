package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// TODO: реализовать тесты для Gateway Controller
@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingGatewayControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private BookingClient bookingClient;
    private static final String REQUEST_USER_HEADER = "X-Sharer-User-Id";

    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;
    private long anyBookeOrOwnerId;
    private long anyItemId;
    private long anyBookingId;

    private LocalDateTime ldt;

    @BeforeEach
    public void preTestInitialization() {
        anyBookeOrOwnerId = 9999;
        anyItemId = 111;
        anyBookingId = 1515;

        ldt = LocalDateTime.of(2020, 10, 12, 15, 45, 30);
        bookingRequestDto = BookingRequestDto.builder()
                .itemId(anyItemId)
                .start(ldt)
                .end(ldt.plusDays(1))
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(anyBookingId)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(ItemBookingDto.of(anyItemId, "item-name"))
                .booker(UserBookingDto.of(anyBookeOrOwnerId))
                .status(Status.WAITING)
                .build();
    }

    @ParameterizedTest
    @MethodSource("giveArgsFor_T0010_NS01")
    public void test_T0010_NS01_createBooking_invalidContent(final String content, final String handler) throws Exception {
        long anyBookerId = 9999;

        String response = mvc.perform(post("/bookings")
                        .content(content)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyBookerId))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains(handler));
    }

    private static Stream<Arguments> giveArgsFor_T0010_NS01() throws Exception {
        return Stream.of(
                arguments("", "Required request body is missing"),
                arguments("{\"itemId\": \"10\",\"end\":\"2020-10-12T19:45:30\"}", "Validation failed for argument"),
                arguments("{\"itemId\": \"10\",\"start\":\"2020-10-12T17:45:30\"}", "Validation failed for argument"),
                arguments("{\"itemId\": \"10\"}", "Validation failed for argument"),
                arguments("{\"start\":\"2020-10-12T17:45:30\"}", "Validation failed for argument"),
                arguments("{\"end\":\"2020-10-12T19:45:30\"}", "Validation failed for argument"));
    }
}
