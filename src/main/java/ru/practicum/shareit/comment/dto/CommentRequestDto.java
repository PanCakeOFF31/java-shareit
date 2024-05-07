package ru.practicum.shareit.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    @NotBlank
    @Size(max = 1024, message = "Comment.text - Минимальная длина имени - {min}, а максимальная {max} символов")
    private String text;
}
