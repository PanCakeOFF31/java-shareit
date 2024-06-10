package ru.practicum.shareit.item.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long requestId = 17L;
        ItemRequestDto itemRequestDto =
                ItemRequestDto.of("item-name", "some-description", false, requestId);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemRequestDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isFalse();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").asString().isEqualTo(String.valueOf(requestId));

        assertThat(result).isStrictlyEqualToJson("{\"name\":\"item-name\",\"description\":\"some-description\",\"available\":false,\"requestId\":17}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        ItemRequestDto itemBooking = ItemRequestDto.of();

        JsonContent<ItemRequestDto> result = json.write(itemBooking);

        assertThat(result).extractingJsonPathStringValue("$.name").isNullOrEmpty();
        assertThat(result).extractingJsonPathStringValue("$.description").isNullOrEmpty();
        assertThat(result).extractingJsonPathBooleanValue("$.available").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isNull();

        assertThat(result).isStrictlyEqualToJson("{\"name\":null,\"description\":null,\"available\":null,\"requestId\":null}");
    }
}