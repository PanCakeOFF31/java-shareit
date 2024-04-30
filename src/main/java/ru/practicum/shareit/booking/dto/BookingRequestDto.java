package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
