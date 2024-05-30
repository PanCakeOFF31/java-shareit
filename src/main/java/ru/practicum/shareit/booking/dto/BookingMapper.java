package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {
    public static BookingResponseDto mapToBookingResponseDto(final Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemBookingDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .booker(UserBookingDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingResponseDto> mapToBookingResponseDto(final Iterable<Booking> bookings) {
        List<BookingResponseDto> dtos = new ArrayList<>();

        for (Booking booking : bookings) {
            dtos.add(mapToBookingResponseDto(booking));
        }

        return dtos;
    }

    public static Booking mapToBooking(final BookingRequestDto bookingDto, final long userId) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(Item.builder()
                        .id(bookingDto.getItemId()).build())
                .booker(User.builder().id(userId).build())
                .status(Status.WAITING)
                .build();
    }
}
