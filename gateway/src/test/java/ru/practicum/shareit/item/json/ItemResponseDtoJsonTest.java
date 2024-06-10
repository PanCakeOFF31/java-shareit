package ru.practicum.shareit.item.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOrderResponseDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemResponseDtoJsonTest {
    private final JacksonTester<ItemResponseDto> json;
    private final ObjectMapper objectMapper;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long itemId = 99L;
        long requestId = 17L;
        long lastBookingId = 13L;
        long nextBookingId = 44L;
        long bookerId = 74L;
        long comment1Id = 133L;
        long comment2Id = 12L;

        LocalDateTime ldt = LocalDateTime.of(2020, 10, 12, 15, 45, 30);

        BookingOrderResponseDto nextBooking = BookingOrderResponseDto.of(nextBookingId, bookerId, ldt, ldt.plusHours(1));
        BookingOrderResponseDto lastBooking = BookingOrderResponseDto.of(lastBookingId, bookerId, ldt.plusHours(2), ldt.plusHours(4));
        CommentResponseDto comment1 = CommentResponseDto.of(comment1Id, "some-text", "maxim", ldt.plusDays(1));
        CommentResponseDto comment2 = CommentResponseDto.of(comment2Id, "some-text", "martin", ldt.plusDays(2));

        List<CommentResponseDto> comments = List.of(comment1, comment2);

        ItemResponseDto itemResponseDto =
                ItemResponseDto.of(itemId, "item-name", "some-description", false, lastBooking, nextBooking, comments, requestId);

        JsonContent<ItemResponseDto> result = json.write(itemResponseDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemResponseDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemResponseDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isFalse();

        assertThat(result).extractingJsonPathValue("$.lastBooking").isNotNull();
        assertThat(result).extractingJsonPathValue("$.lastBooking.id").asString().isEqualTo(String.valueOf(lastBookingId));
        assertThat(result).extractingJsonPathValue("$.lastBooking.bookerId").asString().isEqualTo(String.valueOf(bookerId));
        assertThat(result).extractingJsonPathValue("$.lastBooking.start").isEqualTo(lastBooking.getStart().toString());
        assertThat(result).extractingJsonPathValue("$.lastBooking.end").isEqualTo(lastBooking.getEnd().toString());

        assertThat(result).extractingJsonPathValue("$.nextBooking").isNotNull();
        assertThat(result).extractingJsonPathArrayValue("$.comments.length(2)");
        assertThat(result).extractingJsonPathValue("$.comments.[1]").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").asString().isEqualTo(String.valueOf(requestId));

        assertThat(result).isStrictlyEqualToJson("{\"id\":99,\"name\":\"item-name\",\"description\":\"some-description\",\"available\":false,\"lastBooking\":{\"id\":13,\"bookerId\":74,\"start\":\"2020-10-12T17:45:30\",\"end\":\"2020-10-12T19:45:30\"},\"nextBooking\":{\"id\":44,\"bookerId\":74,\"start\":\"2020-10-12T15:45:30\",\"end\":\"2020-10-12T16:45:30\"},\"comments\":[{\"id\":133,\"text\":\"some-text\",\"authorName\":\"maxim\",\"created\":\"2020-10-13T15:45:30\"},{\"id\":12,\"text\":\"some-text\",\"authorName\":\"martin\",\"created\":\"2020-10-14T15:45:30\"}],\"requestId\":17}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        ItemResponseDto itemBooking = ItemResponseDto.of();

        JsonContent<ItemResponseDto> result = json.write(itemBooking);

        assertThat(result).extractingJsonPathStringValue("$.name").isNullOrEmpty();
        assertThat(result).extractingJsonPathStringValue("$.description").isNullOrEmpty();
        assertThat(result).extractingJsonPathBooleanValue("$.available").isNull();
        assertThat(result).extractingJsonPathValue("$.lastBooking", BookingOrderResponseDto.class).isNull();
        assertThat(result).extractingJsonPathValue("$.nextBooking", BookingOrderResponseDto.class).isNull();
        assertThat(result).extractingJsonPathArrayValue("$.comments").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isNull();

        assertThat(result).isStrictlyEqualToJson("{\"id\":0,\"name\":null,\"description\":null,\"available\":null,\"lastBooking\":null,\"nextBooking\":null,\"comments\":null,\"requestId\":null}");
    }
}