package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class Item {
    private long id;
    @NotBlank
    @Size(max = 64)
    private String name;
    @NotBlank
    @Size(max = 1024)
    private String description;
    @NotNull
    private Boolean available;
    private long owner;

    public Item(final Item otherItem) {
        this.id = otherItem.id;
        this.name = otherItem.name;
        this.description = otherItem.description;
        this.available = otherItem.available;
        this.owner = otherItem.owner;
    }
}
