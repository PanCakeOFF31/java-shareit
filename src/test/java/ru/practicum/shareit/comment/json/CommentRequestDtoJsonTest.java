package ru.practicum.shareit.comment.json;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentRequestDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRequestDtoJsonTest {
    private final JacksonTester<CommentRequestDto> json;

    @Test
    public void test_T0010_PS01_foolField() throws IOException {
        CommentRequestDto commentRequest = new CommentRequestDto("some-text");

        JsonContent<CommentRequestDto> result = json.write(commentRequest);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentRequest.getText());
        assertThat(result).isStrictlyEqualToJson("{\"text\":\"some-text\"}");
    }

    @Test
    public void test_T0010_NS01_emptyField() throws IOException {
        CommentRequestDto commentRequest = new CommentRequestDto();

        JsonContent<CommentRequestDto> result = json.write(commentRequest);

        assertThat(result).extractingJsonPathStringValue("$.text").isNull();
        assertThat(result).isStrictlyEqualToJson("{\"text\":null}");
    }
}