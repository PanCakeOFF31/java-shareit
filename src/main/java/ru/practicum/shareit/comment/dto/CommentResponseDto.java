package ru.practicum.shareit.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

}
