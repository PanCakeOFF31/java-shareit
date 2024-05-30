package ru.practicum.shareit.request.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ReqRequestDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReqRequestDtoJsonTest {
    private final JacksonTester<ReqRequestDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        ReqRequestDto reqRequestDto = ReqRequestDto.of("some-description");

        JsonContent<ReqRequestDto> result = json.write(reqRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(reqRequestDto.getDescription());
        assertThat(result).isStrictlyEqualToJson("{\"description\":\"some-description\"}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        ReqRequestDto reqRequestDto = ReqRequestDto.of();

        JsonContent<ReqRequestDto> result = json.write(reqRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
        assertThat(result).isStrictlyEqualToJson("{\"description\":null}");
    }
}