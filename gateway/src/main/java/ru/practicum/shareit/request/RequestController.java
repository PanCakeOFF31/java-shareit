package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ReqRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody final ReqRequestDto reqRequestDto,
                                                @RequestHeader("X-Sharer-User-Id") final Long requesterId) {
        log.debug("/requests - POST: createRequest({}, {})", reqRequestDto, requesterId);
        return requestClient.createRequest(reqRequestDto, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsForRequester(@RequestHeader("X-Sharer-User-Id") final Long requesterId,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                                          @RequestParam(defaultValue = "10") @Positive final int size) {
        log.debug("/requests?from={}&size={} - GET: getRequestsForRequester({}, {}, {})", from, size, requesterId, from, size);
        log.debug("/requests from requesterId = {}", requesterId);

        return requestClient.getRequestsForRequester(requesterId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") final Long requesterId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                                 @RequestParam(defaultValue = "10") @Positive final int size) {
        log.debug("/requests/all?from={}&size={} - GET: getRequests({}, {}, {})", from, size, requesterId, from, size);
        log.debug("/requests/all from requesterId = {}", requesterId);

        return requestClient.getAllRequests(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestByRequestId(@RequestHeader("X-Sharer-User-Id") final Long requesterId,
                                                        @PathVariable final Long requestId) {
        log.debug("/requests/{} - GET: getRequestByRequestId({}, {})", requestId, requesterId, requesterId);
        log.debug("/requests/{requestId} from requesterId = {}", requesterId);
        return requestClient.getRequestByRequestId(requestId, requesterId);
    }
}
