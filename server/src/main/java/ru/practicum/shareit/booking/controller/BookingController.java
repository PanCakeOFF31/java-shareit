package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(@RequestBody final BookingRequestDto bookingDto,
                                            @RequestHeader("X-Sharer-User-Id") final Long bookerId) {
        log.debug("/bookings - POST: createBooking({})", bookingDto);

        return bookingService.createBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto toBook(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                     @PathVariable final long bookingId,
                                     @RequestParam final boolean approved) {
        log.debug("/bookings/{}?approved={} - PATCH: toBook({}, {}, {})",
                bookingId, approved, ownerId, bookingId, approved);
        return bookingService.toBook(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") final Long bookerOrOwnerId,
                                         @PathVariable final long bookingId) {
        log.debug("/bookings/{} - GET: getBooking({}, {})", bookingId, bookerOrOwnerId, bookingId);
        return bookingService.getBookingDto(bookingId, bookerOrOwnerId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingByBooker(@RequestHeader("X-Sharer-User-Id") final Long bookerId,
                                                          @RequestParam final State state,
                                                          @RequestParam final int from,
                                                          @RequestParam final int size) {
        log.debug("/bookings?state={}&from={}&size={} - GET: getAllBookingByUser({}, {}, {}, {})", state, from, size, bookerId, state, from, size);
        return bookingService.getAllBookingByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                                         @RequestParam final State state,
                                                         @RequestParam final int from,
                                                         @RequestParam final int size) {
        log.debug("/bookings/owner?state={}&from={}&size={} - GET: getAllBookingByUser({}, {}, {}, {})", state, from, size, ownerId, state, from, size);
        return bookingService.getAllBookingByOwner(ownerId, state, from, size);
    }
}
