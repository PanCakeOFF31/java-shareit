package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class User {
    private long id;
    @NotBlank
    @Size(max = 64)
    private String name;
    @NotBlank
    @Email
    @Size(max = 128)
    private String email;

    public User(final User otherUser) {
        this.id = otherUser.id;
        this.name = otherUser.name;
        this.email = otherUser.email;
    }
}
