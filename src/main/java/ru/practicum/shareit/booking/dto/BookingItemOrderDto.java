package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingItemOrderDto {
    private long id;
    private long bookerId;
}
