package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class UserRequestDto {
    @NotBlank
    @Size(max = 128, message = "User.name - Минимальная длина имени - {min}, а максимальная {max} символов")
    private String name;

    @Email(message = "User.email - почта не соответствует шаблону @Email")
    @NotBlank
    @Size(max = 256, message = "User.email - Минимальная длина почты - {min}, а максимальная {max} символов")
    private String email;

    public UserRequestDto(final UserRequestDto otherUser) {
        this.name = otherUser.name;
        this.email = otherUser.email;
    }
}
