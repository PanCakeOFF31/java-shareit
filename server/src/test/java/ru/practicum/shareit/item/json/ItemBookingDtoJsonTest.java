package ru.practicum.shareit.item.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemBookingDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemBookingDtoJsonTest {
    private final JacksonTester<ItemBookingDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long itemId = 99L;
        ItemBookingDto itemBookingDto = new ItemBookingDto(itemId, "item-name");

        JsonContent<ItemBookingDto> result = json.write(itemBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").asString().isEqualTo(String.valueOf(itemId));
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemBookingDto.getName());

        assertThat(result).isStrictlyEqualToJson("{\"id\":99,\"name\":\"item-name\"}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        ItemBookingDto itemBooking = new ItemBookingDto();

        JsonContent<ItemBookingDto> result = json.write(itemBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").asString().isEqualTo(String.valueOf(0));
        assertThat(result).extractingJsonPathStringValue("$.name").isNullOrEmpty();

        assertThat(result).isStrictlyEqualToJson("{\"id\":0,\"name\":null}");
    }
}