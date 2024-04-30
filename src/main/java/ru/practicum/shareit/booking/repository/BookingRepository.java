package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking as b " +
            "LEFT JOIN FETCH User as u ON b.booker.id = u.id " +
            "LEFT JOIN FETCH Item as it ON b.item.id = it.id " +
            "WHERE b.id = :bookingId")
    Optional<Booking> findBookingByIdFetch(final long bookingId);

    @Query("select b " +
            "from Booking as b " +
            "LEFT JOIN FETCH User as u ON b.booker.id = u.id " +
            "LEFT JOIN FETCH Item as it ON b.item.id = it.id " +
            "where b.id = :booking_id and " +
            "(b.booker.id = :user_id OR b.item.owner.id = :user_id)"
    )
    Optional<Booking> findByIdAndBookerIdOrOwnerId(@Param("booking_id") final long bookingId,
                                                   @Param("user_id") final long bookerOrOwnerId);


    // State.Past
    List<Booking> findByBookerIdAndEndLessThanOrderByStartDesc
    (final long bookerId, final LocalDateTime currentDateTime, final Pageable pageable);

    // State.Current
    List<Booking> findByBookerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc
    (final long bookerId, final LocalDateTime currentDateTime1, final LocalDateTime currentDateTime2, final Pageable pageable);

    // State.   Future
    List<Booking> findByBookerIdAndStartGreaterThanEqualOrderByStartDesc
    (final long bookerId, final LocalDateTime currentDateTime, final Pageable pageable);

    // State.Status
    List<Booking> findByBookerIdAndStatusOrderByStartDesc
    (final long bookerId, final Status status, final Pageable pageable);

    // State.All
    List<Booking> findByBookerIdOrderByStartDesc
    (final long bookerId, Pageable pageable);


    //  State.Past
    List<Booking> findByItemOwnerIdAndEndLessThanOrderByStartDesc
    (final long bookerId, final LocalDateTime currentDateTime, final Pageable pageable);

    //  State.Current
    List<Booking> findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc
    (final long bookerId, final LocalDateTime currentDateTime1, final LocalDateTime currentDateTime2, final Pageable pageable);

    //  State.Future
    List<Booking> findByItemOwnerIdAndStartGreaterThanEqualOrderByStartDesc
    (final long bookerId, final LocalDateTime currentDateTime, final Pageable pageable);

    //  State.Status
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc
    (final long bookerId, final Status status, final Pageable pageable);

    //  State.All
    List<Booking> findByItemOwnerIdOrderByStartDesc
    (final long ownerId, final Pageable pageable);

    //    Last
    Optional<Booking> findTop1ByItemIdAndEndLessThanEqualOrderByEndDesc
    (final long itemId, final LocalDateTime currentDateTime);

    //    Only-Last
    Optional<Booking> findTop1ByItemIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByEndDesc
    (final long itemId, final LocalDateTime ldt1, final LocalDateTime ldt2);

    //    Next
    Optional<Booking> findTop1ByItemIdAndStartGreaterThanEqualAndStatusInOrderByStartAsc
    (final long itemId, final LocalDateTime currentDateTime, Collection<Status> statuses);

    boolean existsBookingByItemIdAndBookerIdAndStatusIsNotAndEndLessThan
            (final long itemId, final long bookerId, final Status status, final LocalDateTime ldc);
}
