package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemBookingDto item;
    private UserBookingDto booker;
    private Status status;
}
