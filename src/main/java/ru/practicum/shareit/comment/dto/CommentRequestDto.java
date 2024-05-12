package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentRequestDto {
    @NotBlank
    @Size(min = 10, max = 1024, message = "Comment.text - Минимальная длина имени - {min}, а максимальная {max} символов")
    private String text;
}
