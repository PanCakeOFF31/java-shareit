package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
public class CommentResponseDto {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

}
