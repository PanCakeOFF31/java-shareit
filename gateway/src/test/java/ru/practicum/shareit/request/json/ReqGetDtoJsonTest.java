package ru.practicum.shareit.request.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemReqGetDto;
import ru.practicum.shareit.request.dto.ReqGetDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReqGetDtoJsonTest {
    private final JacksonTester<ReqGetDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long requestId = 99L;
        long itemId1 = 42L;
        long itemId2 = 45L;

        LocalDateTime ldt = LocalDateTime.of(2020, 10, 12, 15, 45, 30);

        ItemReqGetDto item1 = ItemReqGetDto.of(itemId1, "item-1", "item-1-description", true, requestId);
        ItemReqGetDto item2 = ItemReqGetDto.of(itemId2, "item-2", "item-2-description", true, requestId);

        ReqGetDto reqGetDto = ReqGetDto.of(requestId, "some-description", ldt, List.of(item1, item2));

        JsonContent<ReqGetDto> result = json.write(reqGetDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").asString().isEqualTo(String.valueOf(requestId));
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(reqGetDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(reqGetDto.getCreated().toString());
        assertThat(result).extractingJsonPathNumberValue("$.items.length()").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[1].name").isEqualTo(item2.getName());
        assertThat(result).extractingJsonPathValue("$.items[0]")
                .extracting("name", "description")
                .isEqualTo(List.of(item1.getName(), item1.getDescription()));

        assertThat(result).isStrictlyEqualToJson("{\"id\":99,\"description\":\"some-description\",\"created\":\"2020-10-12T15:45:30\",\"items\":[{\"id\":42,\"name\":\"item-1\",\"description\":\"item-1-description\",\"available\":true,\"requestId\":99},{\"id\":45,\"name\":\"item-2\",\"description\":\"item-2-description\",\"available\":true,\"requestId\":99}]}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        ReqGetDto reqGetDto = ReqGetDto.of();

        JsonContent<ReqGetDto> result = json.write(reqGetDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
        assertThat(result).extractingJsonPathStringValue("$.created").isNull();
        assertThat(result).extractingJsonPathArrayValue("$.items").isNull();

        assertThat(result).isStrictlyEqualToJson("{\"id\":0,\"description\":null,\"created\":null,\"items\":null}");
    }
}