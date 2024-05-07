package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @NotBlank
    @Size(max = 128, message = "Item.name - Минимальная длина имени - {min}, а максимальная {max} символов")
    private String name;

    @NotBlank
    @Size(max = 1024, message = "Item.name - Минимальная длина имени - {min}, а максимальная {max} символов")
    private String description;

    @NotNull
    private Boolean available;

    public ItemRequestDto(final ItemRequestDto otherItem) {
        this.name = otherItem.name;
        this.description = otherItem.description;
        this.available = otherItem.available;
    }
}
