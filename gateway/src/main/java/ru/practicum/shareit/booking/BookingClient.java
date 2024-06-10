package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.exception.UnsupportedStateException;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

import static ru.practicum.shareit.common.ClientPath.BASE_SLASH_PATH;
import static ru.practicum.shareit.common.ClientPath.BASE_SPACE_PATH;

@Slf4j
@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";
    private static final String UNSUPPORTED_STATUS = "Данный статус '%s' не поддерживается";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, @Autowired RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );

        log.debug("BookingClient(URL={}{})", serverUrl, API_PREFIX);
    }

    public ResponseEntity<Object> createBooking(BookingRequestDto bookingRequestDto, long bookerId) {
        log.debug("BookingClient - baseClient.createBooking({}, {})", bookingRequestDto, bookerId);
        return post(BASE_SPACE_PATH, bookerId, bookingRequestDto);
    }

    public ResponseEntity<Object> toBook(long ownerId, long bookingId, boolean approved) {
        log.debug("BookingClient - baseClient.toBook({}, {}, {})", ownerId, bookingId, approved);
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch(BASE_SLASH_PATH + bookingId + "?approved={approved}", ownerId, parameters, null);
    }

    public ResponseEntity<Object> getBooking(long bookingId, long bookerOrOwnerId) {
        log.debug("BookingClient - baseClient.getBooking({}, {})", bookingId, bookerOrOwnerId);
        return get(BASE_SLASH_PATH + bookingId, bookerOrOwnerId);
    }

    public ResponseEntity<Object> getAllBookingByBooker(long bookerId, String state, int from, int size) {
        log.debug("BookingClient - baseClient.getAllBookingByBooker({}, {}, {}, {})", bookerId, state, from, size);

        stateValidation(state);
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );

        return get("?state={state}&from={from}&size={size}", bookerId, parameters);
    }

    public ResponseEntity<Object> getAllBookingByOwner(long ownerId, String state, int from, int size) {
        log.debug("BookingClient - baseClient.getAllBookingByOwner({}, {}, {}, {})", ownerId, state, from, size);

        stateValidation(state);
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );

        return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
    }

    private void stateValidation(final String state) throws UnsupportedStateException {
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException ignore) {
            String message = String.format(UNSUPPORTED_STATUS, state);
            log.warn(message);
            throw new UnsupportedStateException(message);
        }
    }
}
