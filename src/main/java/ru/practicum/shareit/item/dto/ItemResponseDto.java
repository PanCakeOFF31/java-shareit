package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingItemOrderDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingItemOrderDto lastBooking;
    private BookingItemOrderDto nextBooking;
    private List<CommentResponseDto> comments;

    public ItemResponseDto(final ItemResponseDto otherItem) {
        this.id = otherItem.id;
        this.name = otherItem.name;
        this.description = otherItem.description;
        this.available = otherItem.available;
    }
}
