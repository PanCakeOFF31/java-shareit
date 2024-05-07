package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemBookingDto {
    private long id;
    private String name;
}
