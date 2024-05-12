package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDto {
    @NotBlank
    @Size(max = 128, message = "Item.name - Минимальная длина имени - {min}, а максимальная {max} символов")
    private String name;

    @NotBlank
    @Size(max = 1024, message = "Item.name - Минимальная длина имени - {min}, а максимальная {max} символов")
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;
}
