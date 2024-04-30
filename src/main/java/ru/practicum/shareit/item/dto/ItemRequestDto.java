package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    @Size(max = 128)
    private String name;
    @Size(max = 1024)
    private String description;
    private Boolean available;

    public ItemRequestDto(final ItemRequestDto otherItem) {
        this.id = otherItem.id;
        this.name = otherItem.name;
        this.description = otherItem.description;
        this.available = otherItem.available;
    }
}
