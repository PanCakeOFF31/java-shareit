package ru.practicum.shareit.user.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserResponseDtoJsonTest {
    private final JacksonTester<UserResponseDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long userId = 99L;
        UserResponseDto userResponseDto = UserResponseDto.of(userId, "user-name", "mail@yandex.ru");

        JsonContent<UserResponseDto> result = json.write(userResponseDto);

        System.out.println(result);

        assertThat(result).extractingJsonPathNumberValue("$.id").asString().isEqualTo(String.valueOf(userId));
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userResponseDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userResponseDto.getEmail());

        assertThat(result).isStrictlyEqualToJson("{\"id\":99,\"name\":\"user-name\",\"email\":\"mail@yandex.ru\"}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        UserResponseDto userResponseDto = UserResponseDto.of();

        JsonContent<UserResponseDto> result = json.write(userResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").asString().isEqualTo(String.valueOf(0));
        assertThat(result).extractingJsonPathStringValue("$.name").isNullOrEmpty();
        assertThat(result).extractingJsonPathStringValue("$.email").isNullOrEmpty();

        assertThat(result).isStrictlyEqualToJson("{\"id\":0,\"name\":null,\"email\":null}");
    }
}