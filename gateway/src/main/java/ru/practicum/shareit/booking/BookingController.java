package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody final BookingRequestDto bookingDto,
                                                @RequestHeader("X-Sharer-User-Id") final Long bookerId) {
        log.debug("/bookings - POST: createBooking({})", bookingDto);
        return bookingClient.createBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> toBook(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                         @PathVariable final long bookingId,
                                         @RequestParam final boolean approved) {
        log.debug("/bookings/{}?approved={} - PATCH: toBook({}, {}, {})",
                bookingId, approved, ownerId, bookingId, approved);
        return bookingClient.toBook(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") final Long bookerOrOwnerId,
                                             @PathVariable final long bookingId) {
        log.debug("/bookings/{} - GET: getBooking({}, {})", bookingId, bookerOrOwnerId, bookingId);
        return bookingClient.getBooking(bookingId, bookerOrOwnerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingByBooker(@RequestHeader("X-Sharer-User-Id") final Long bookerId,
                                                        @RequestParam(required = false, defaultValue = "ALL") final String state,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                                        @RequestParam(defaultValue = "10") @Positive final int size) {
        log.debug("/bookings?state={} - GET: getAllBookingByUser({}, {}, {}, {})", state, bookerId, state, from, size);
        return bookingClient.getAllBookingByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                                       @RequestParam(required = false, defaultValue = "ALL") final String state,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                                       @RequestParam(defaultValue = "10") @Positive final int size) {
        log.debug("/bookings/owner?state={}&from={}&size={} - GET: getAllBookingByUser({}, {}, {}, {})", state, from, size, ownerId, state, from, size);
        return bookingClient.getAllBookingByOwner(ownerId, state, from, size);
    }
}
