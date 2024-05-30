package ru.practicum.shareit.comment.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentResponseDtoJsonTest {
    private final JacksonTester<CommentResponseDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        long commentId = 17L;
        LocalDateTime ldt = LocalDateTime.of(2020, 10, 12, 15, 45, 30);

        CommentResponseDto commentResponseDto = CommentResponseDto.of(commentId, "some-text", "author", ldt);

        JsonContent<CommentResponseDto> result = json.write(commentResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentResponseDto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(commentResponseDto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(commentResponseDto.getCreated().toString());

        assertThat(result).isStrictlyEqualToJson("{\"id\":17,\"text\":\"some-text\",\"authorName\":\"author\",\"created\":\"2020-10-12T15:45:30\"}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        CommentResponseDto commentResponseDto = CommentResponseDto.of();

        JsonContent<CommentResponseDto> result = json.write(commentResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathStringValue("$.text").isNull();
        assertThat(result).extractingJsonPathStringValue("$.authorName").isNull();
        assertThat(result).extractingJsonPathStringValue("$.created").isNull();

        assertThat(result).isStrictlyEqualToJson("{\"id\":0,\"text\":null,\"authorName\":null,\"created\":null}");
    }
}