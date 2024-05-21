package ru.practicum.shareit.item.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemReqGetDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemReqGetDtoJsonTest {
    private final JacksonTester<ItemReqGetDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long itemId = 99L;
        long requestId = 17L;
        ItemReqGetDto itemReqGetDto =
                new ItemReqGetDto(itemId, "item-name", "some-description", false, requestId);

        JsonContent<ItemReqGetDto> result = json.write(itemReqGetDto);

        assertThat(result).extractingJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemReqGetDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemReqGetDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isFalse();
        assertThat(result).extractingJsonPathNumberValue("$.requestId");


        assertThat(result).isStrictlyEqualToJson("{\"id\":99,\"name\":\"item-name\",\"description\":\"some-description\",\"available\":false,\"requestId\":17}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        ItemReqGetDto itemBooking = new ItemReqGetDto();

        JsonContent<ItemReqGetDto> result = json.write(itemBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathStringValue("$.name").isNullOrEmpty();
        assertThat(result).extractingJsonPathStringValue("$.description").isNullOrEmpty();
        assertThat(result).extractingJsonPathBooleanValue("$.available").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.requestId");

        assertThat(result).isStrictlyEqualToJson("{\"id\":0,\"name\":null,\"description\":null,\"available\":null,\"requestId\":0}");
    }
}