package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static CommentResponseDto mapToCommentResponseDto(final Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentResponseDto> mapToCommentResponseDto(final Iterable<Comment> comments) {
        List<CommentResponseDto> dtos = new ArrayList<>();

        for (Comment comment : comments) {
            dtos.add(mapToCommentResponseDto(comment));
        }
        return dtos;
    }

    public static Comment mapToComment(final CommentRequestDto commentRequestDto) {
        return Comment.builder()
                .text(commentRequestDto.getText())
                .build();
    }
}
