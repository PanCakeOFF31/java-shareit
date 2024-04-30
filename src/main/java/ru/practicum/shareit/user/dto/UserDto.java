package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private long id;

    @Size(max = 128)
    private String name;

    @Email
    @Size(max = 256)
    private String email;

    public UserDto(final UserDto otherUser) {
        this.name = otherUser.name;
        this.email = otherUser.email;
    }
}
