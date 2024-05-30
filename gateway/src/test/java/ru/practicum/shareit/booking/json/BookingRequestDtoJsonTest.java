package ru.practicum.shareit.booking.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRequestDtoJsonTest {
    private final JacksonTester<BookingRequestDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long itemId = 17L;
        LocalDateTime ldt = LocalDateTime.of(2020, 10, 12, 15, 45, 30);

        BookingRequestDto bookingRequestDto =
                BookingRequestDto.of(itemId, ldt, ldt.plusDays(1));

        JsonContent<BookingRequestDto> result = json.write(bookingRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingRequestDto.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingRequestDto.getEnd().toString());

        assertThat(result).isStrictlyEqualToJson("{\"itemId\":17,\"start\":\"2020-10-12T15:45:30\",\"end\":\"2020-10-13T15:45:30\"}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        BookingRequestDto bookingRequestDto = BookingRequestDto.of();

        JsonContent<BookingRequestDto> result = json.write(bookingRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isNull();
        assertThat(result).extractingJsonPathStringValue("$.start").isNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNull();

        assertThat(result).isStrictlyEqualToJson("{\"itemId\":null,\"start\":null,\"end\":null}");
    }
}