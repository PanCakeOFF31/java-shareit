package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    @Size(max = 1024)
    private String description;
    private Boolean available;

    public ItemDto(final ItemDto otherItem) {
        this.id = otherItem.id;
        this.name = otherItem.name;
        this.description = otherItem.description;
        this.available = otherItem.available;
    }
}
