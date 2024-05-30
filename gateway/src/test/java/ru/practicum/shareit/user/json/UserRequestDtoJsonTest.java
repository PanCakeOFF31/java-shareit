package ru.practicum.shareit.user.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRequestDtoJsonTest {
    private final JacksonTester<UserRequestDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        UserRequestDto userRequestDto = UserRequestDto.of("user-name", "mail@yandex.ru");

        JsonContent<UserRequestDto> result = json.write(userRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userRequestDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userRequestDto.getEmail());
        assertThat(result).isStrictlyEqualToJson("{\"name\":\"user-name\",\"email\":\"mail@yandex.ru\"}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        UserRequestDto userRequestDto = UserRequestDto.of();

        JsonContent<UserRequestDto> result = json.write(userRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isNullOrEmpty();
        assertThat(result).extractingJsonPathStringValue("$.email").isNullOrEmpty();

        assertThat(result).isStrictlyEqualToJson("{\"name\":null,\"email\":null}");
    }
}