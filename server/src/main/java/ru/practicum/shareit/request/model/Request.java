package ru.practicum.shareit.request.model;

import lombok.*;
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
@Table(name = "request")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 1024, nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime created;

    @ToString.Exclude
    @JoinColumn(name = "requester_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    private User requester;

    public Request(final Request otherRequest) {
        this.id = otherRequest.id;
        this.description = otherRequest.description;
        this.created = otherRequest.created;
        this.requester = otherRequest.requester;
    }
}
