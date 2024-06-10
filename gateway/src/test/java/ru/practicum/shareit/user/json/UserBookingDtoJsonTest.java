package ru.practicum.shareit.user.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserBookingDtoJsonTest {
    private final JacksonTester<UserBookingDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long userId = 99L;
        UserBookingDto userResponseDto = UserBookingDto.of(userId);

        JsonContent<UserBookingDto> result = json.write(userResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id");
        assertThat(result).isStrictlyEqualToJson("{\"id\":99}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        UserBookingDto userResponseDto = UserBookingDto.of();

        JsonContent<UserBookingDto> result = json.write(userResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id");
        assertThat(result).isStrictlyEqualToJson("{\"id\":0}");
    }
}