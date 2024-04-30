package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;
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
                                            @RequestHeader("X-Sharer-User-Id") final long userId) {
        log.debug("/bookings - POST: createBooking({})", bookingDto);

        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto toBook(@RequestHeader("X-Sharer-User-Id") final long ownerId,
                                     @PathVariable final long bookingId,
                                     @RequestParam final boolean approved) {
        log.debug("/bookings/{}?approved={} - PATCH: toBook({}, {}, {})",
                bookingId, approved, ownerId, bookingId, approved);
        return bookingService.toBook(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") final long bookerOrOwnerId,
                                         @PathVariable final long bookingId) {
        log.debug("/bookings/{} - GET: getBooking({}, {})", bookingId, bookerOrOwnerId, bookingId);
        return bookingService.getBookingDto(bookerOrOwnerId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingByUser(@RequestHeader("X-Sharer-User-Id") final long bookerId,
                                                        @RequestParam(required = false, defaultValue = "ALL") final String state) {
        log.debug("/bookings?state={} - GET: getAllBookingByUser({}, {})", state, bookerId, state);
        return bookingService.getAllBookingByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") final long ownerId,
                                                         @RequestParam(required = false, defaultValue = "ALL") final String state) {
        log.debug("/bookings?state={} - GET: getAllBookingByOwner({}, {})", state, ownerId, state);
        return bookingService.getAllBookingByOwner(ownerId, state);
    }

    //    TODO: служебный ENDPOINT
    @GetMapping("/all")
    public Collection<BookingResponseDto> getAll() {
        log.debug("/bookings/all - GET: getAll()");
        return bookingService.getAll();
    }
}
