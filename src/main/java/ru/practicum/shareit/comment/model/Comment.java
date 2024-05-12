package ru.practicum.shareit.comment.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 1024, nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime created;

    @ToString.Exclude
    @JoinColumn(name = "item_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Item.class)
    private Item item;

    @ToString.Exclude
    @JoinColumn(name = "author_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    private User author;

    public Comment(Comment otherComment) {
        this.id = otherComment.id;
        this.text = otherComment.text;
        this.item = otherComment.item;
        this.author = otherComment.author;
    }
}
