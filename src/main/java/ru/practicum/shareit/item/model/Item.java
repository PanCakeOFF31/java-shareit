package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(max = 128)
    @Column(length = 128, nullable = false)
    private String name;

    @NotBlank
    @Size(max = 1024)
    @Column(length = 1024, nullable = false)
    private String description;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private List<Comment> comments = new ArrayList<>();

    @ToString.Exclude
    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    public Item(final Item otherItem) {
        this.id = otherItem.id;
        this.name = otherItem.name;
        this.description = otherItem.description;
        this.available = otherItem.available;
        this.owner = otherItem.owner;
        this.comments = otherItem.comments;
    }
}
