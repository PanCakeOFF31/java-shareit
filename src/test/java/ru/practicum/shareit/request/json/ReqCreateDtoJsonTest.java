package ru.practicum.shareit.request.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ReqCreateDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReqCreateDtoJsonTest {
    private final JacksonTester<ReqCreateDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long requestId = 99L;
        LocalDateTime ldt = LocalDateTime.of(2020, 10, 12, 15, 45, 30);

        ReqCreateDto reqRequestDto = new ReqCreateDto(requestId, "some-description", ldt);

        JsonContent<ReqCreateDto> result = json.write(reqRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").asString().isEqualTo(String.valueOf(requestId));
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(reqRequestDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(reqRequestDto.getCreated().toString());

        assertThat(result).isStrictlyEqualToJson("{\"id\":99,\"description\":\"some-description\",\"created\":\"2020-10-12T15:45:30\"}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        ReqCreateDto reqRequestDto = new ReqCreateDto();

        JsonContent<ReqCreateDto> result = json.write(reqRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
        assertThat(result).extractingJsonPathStringValue("$.created").isNull();

        assertThat(result).isStrictlyEqualToJson("{\"id\":0,\"description\":null,\"created\":null}");
    }
}