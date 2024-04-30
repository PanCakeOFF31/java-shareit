package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(max = 128)
    @Column(length = 128, nullable = false)
    private String name;

    @Email
    @NotBlank
    @Size(max = 256)
    @Column(length = 256, nullable = false, unique = true)
    private String email;

    public User(final User otherUser) {
        this.id = otherUser.id;
        this.name = otherUser.name;
        this.email = otherUser.email;
    }
}
