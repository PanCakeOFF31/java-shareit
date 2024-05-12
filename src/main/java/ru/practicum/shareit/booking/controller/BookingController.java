package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(@Valid @RequestBody final BookingRequestDto bookingDto,
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
                                                          @RequestParam(required = false, defaultValue = "ALL") final String state,
                                                          @RequestParam(defaultValue = "0") final int from,
                                                          @RequestParam(defaultValue = "10") final int size) {
        log.debug("/bookings?state={} - GET: getAllBookingByUser({}, {})", state, bookerId, state);
        return bookingService.getAllBookingByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                                         @RequestParam(required = false, defaultValue = "ALL") final String state,
                                                         @RequestParam(defaultValue = "0") final int from,
                                                         @RequestParam(defaultValue = "10") final int size) {
        log.debug("/bookings?state={} - GET: getAllBookingByOwner({}, {})", state, ownerId, state);
        return bookingService.getAllBookingByOwner(ownerId, state, from, size);
    }
}
