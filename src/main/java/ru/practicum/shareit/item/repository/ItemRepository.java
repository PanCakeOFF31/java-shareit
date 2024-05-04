package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingItemOrderDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findItemByIdAndOwnerId(final long itemId, final long ownerId);

    boolean existsItemByIdAndOwnerId(final long itemId, final long ownerId);

    List<Item> findAllByOwnerIdOrderByIdAsc(final long ownerId, final Pageable pageable);

    @Query(value = "select * " +
            "from item as it " +
            "where (it.description ilike %:text% or it.name ilike %:text%) " +
            "and it.is_available=true",
            nativeQuery = true)
    List<Item> findItemsByNameOrDescriptionText(@Param("text") final String text);

    //        Last booking
    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingItemOrderDto(b.id as id, u.id as bookerId)  " +
            "from Booking as b " +
            "LEFT JOIN FETCH User as u ON b.booker.id = u.id " +
            "LEFT JOIN FETCH Item as it ON b.item.id = it.id " +
            "WHERE b.item.id = :item_id AND b.end <= :now_time " +
            "ORDER BY b.end DESC ")
    List<BookingItemOrderDto> findTopByItemIdAndEndLessThanEqualOrderByEndDesc(
            @Param("item_id") final long itemId, @Param("now_time") final LocalDateTime ldt, Pageable pageable);

    //    Only-Last
    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingItemOrderDto(b.id as id, u.id as bookerId)  " +
            "from Booking as b " +
            "LEFT JOIN FETCH User as u ON b.booker.id = u.id " +
            "LEFT JOIN FETCH Item as it ON b.item.id = it.id " +
            "WHERE b.item.id = :item_id " +
            "AND b.start <= :now_time AND b.end >= :now_time " +
            "ORDER BY b.end DESC ")
    List<BookingItemOrderDto> findTop1ByItemIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByEndDesc(
            @Param("item_id") final long itemId, @Param("now_time") final LocalDateTime ldt1, Pageable pageable);

    //    Next
    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingItemOrderDto(b.id as id, u.id as bookerId)  " +
            "from Booking as b " +
            "LEFT JOIN FETCH User as u ON b.booker.id = u.id " +
            "LEFT JOIN FETCH Item as it ON b.item.id = it.id " +
            "WHERE b.item.id = :item_id " +
            "AND b.start >= :now_time AND b.status IN (:statuses)" +
            "ORDER BY b.start ASC ")
    List<BookingItemOrderDto> findTop1ByItemIdAndStartGreaterThanEqualAndStatusInOrderByStartAsc(
            @Param("item_id") final long itemId, @Param("now_time") final LocalDateTime ldt,
            Collection<Status> statuses, Pageable pageable);

}
