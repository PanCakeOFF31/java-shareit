package ru.practicum.shareit.booking.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOrderResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingOrderResponseDtoJsonTest {
    private final JacksonTester<BookingOrderResponseDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long bookingId = 17L;
        long bookerId = 19L;
        LocalDateTime ldt = LocalDateTime.of(2020, 10, 12, 15, 45, 30);

        BookingOrderResponseDto bookingOrderResponseDto =
                new BookingOrderResponseDto(bookingId, bookerId, ldt, ldt.plusDays(1));

        JsonContent<BookingOrderResponseDto> result = json.write(bookingOrderResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").asString().isEqualTo(String.valueOf(bookingId));
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").asString().isEqualTo(String.valueOf(bookerId));
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingOrderResponseDto.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingOrderResponseDto.getEnd().toString());

        assertThat(result).isStrictlyEqualToJson("{\"id\":17,\"bookerId\":19,\"start\":\"2020-10-12T15:45:30\",\"end\":\"2020-10-13T15:45:30\"}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        BookingOrderResponseDto bookingOrderResponseDto = new BookingOrderResponseDto();

        JsonContent<BookingOrderResponseDto> result = json.write(bookingOrderResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.start").isNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNull();

        assertThat(result).isStrictlyEqualToJson("{\"id\":0,\"bookerId\":0,\"start\":null,\"end\":null}");
    }
}