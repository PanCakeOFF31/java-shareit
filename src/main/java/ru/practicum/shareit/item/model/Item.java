package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 128, nullable = false)
    private String name;

    @Column(length = 1024, nullable = false)
    private String description;

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
