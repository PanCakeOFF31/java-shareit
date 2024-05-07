package ru.practicum.shareit.booking.model;

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
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "booking_start", nullable = false)
    private LocalDateTime start;

    @Column(name = "booking_end", nullable = false)
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Item.class)
    @ToString.Exclude
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @ToString.Exclude
    @JoinColumn(name = "booker_id")
    private User booker;

    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private Status status;

    public Booking(final Booking booking) {
        this.id = booking.id;
        this.start = booking.start;
        this.end = booking.end;
        this.item = booking.item;
        this.booker = booking.booker;
        this.status = booking.status;
    }
}
