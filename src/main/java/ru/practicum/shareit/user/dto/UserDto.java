package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDto {
    @Size(max = 64)
    private String name;
    @Email
    @Size(max = 128)
    private String email;
}
