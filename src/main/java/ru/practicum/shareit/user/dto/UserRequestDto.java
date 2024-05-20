package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserRequestDto {
    @NotBlank
    @Size(max = 128, message = "User.name - Минимальная длина имени - {min}, а максимальная {max} символов")
    private String name;

    @Email(message = "User.email - почта не соответствует шаблону @Email")
    @NotBlank
    @Size(max = 256, message = "User.email - Минимальная длина почты - {min}, а максимальная {max} символов")
    private String email;
}
