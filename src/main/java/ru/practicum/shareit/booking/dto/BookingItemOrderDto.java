package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingItemOrderDto {
    private long id;
    private long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
