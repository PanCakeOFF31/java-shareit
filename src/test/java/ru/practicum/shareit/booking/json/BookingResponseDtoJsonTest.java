package ru.practicum.shareit.booking.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingResponseDtoJsonTest {
    private final JacksonTester<BookingResponseDto> json;
    private final JacksonTester<ItemBookingDto> jsonItem;
    private final ObjectMapper mapper;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long bookingId = 17L;
        long userId = 19L;
        long itemId = 15L;

        ItemBookingDto item = new ItemBookingDto(itemId, "item-name");
        UserBookingDto booker = new UserBookingDto(userId);
        LocalDateTime ldt = LocalDateTime.of(2020, 10, 12, 15, 45, 30);
        BookingResponseDto bookingResponseDto =
                new BookingResponseDto(bookingId, ldt, ldt.plusDays(1), item, booker, Status.WAITING);

        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.item.id");
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingResponseDto.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingResponseDto.getEnd().toString());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingResponseDto.getStatus().toString());

        assertThat(result).isStrictlyEqualToJson("{\"id\":17,\"start\":\"2020-10-12T15:45:30\",\"end\":\"2020-10-13T15:45:30\",\"item\":{\"id\":15,\"name\":\"item-name\"},\"booker\":{\"id\":19},\"status\":\"WAITING\"}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.item").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.booker").isNull();
        assertThat(result).extractingJsonPathStringValue("$.start").isNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNull();
        assertThat(result).extractingJsonPathStringValue("$.status").isNull();

        assertThat(result).isStrictlyEqualToJson("{\"id\":0,\"start\":null,\"end\":null,\"item\":null,\"booker\":null,\"status\":null}");
    }
}