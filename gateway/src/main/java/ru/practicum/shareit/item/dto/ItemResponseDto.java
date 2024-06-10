package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingOrderResponseDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

import java.util.List;

@Data
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
public class ItemResponseDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingOrderResponseDto lastBooking;
    private BookingOrderResponseDto nextBooking;
    private List<CommentResponseDto> comments;
    private Long requestId;

}
