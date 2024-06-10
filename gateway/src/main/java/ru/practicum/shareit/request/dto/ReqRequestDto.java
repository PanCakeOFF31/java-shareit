package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
public class ReqRequestDto {
    @NotBlank
    @Size(min = 10, max = 1024, message = "Request.description - Минимальная длина описания - {min}, а максимальная {max} символов")
    private String description;
}
