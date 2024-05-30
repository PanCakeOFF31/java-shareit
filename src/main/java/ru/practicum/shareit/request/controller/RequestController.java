package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ReqCreateDto;
import ru.practicum.shareit.request.dto.ReqGetDto;
import ru.practicum.shareit.request.dto.ReqRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReqCreateDto createRequest(@Valid @RequestBody final ReqRequestDto reqRequestDto,
                                      @RequestHeader("X-Sharer-User-Id") final Long requesterId) {
        log.debug("/requests - POST: createRequest({}, {})", reqRequestDto, requesterId);
        return requestService.createRequest(reqRequestDto, requesterId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReqGetDto> getRequestsForRequester(@RequestHeader("X-Sharer-User-Id") final Long requesterId,
                                                   @RequestParam(defaultValue = "0") final int from,
                                                   @RequestParam(defaultValue = "10") final int size) {
        log.debug("/requests - GET: getRequestsForRequester({})", requesterId);
        log.debug("/requests from requesterId = {}", requesterId);

        return requestService.getRequestsByRequester(requesterId, from, size);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ReqGetDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") final Long requesterId,
                                          @RequestParam(defaultValue = "0") final int from,
                                          @RequestParam(defaultValue = "10") final int size) {
        log.debug("/requests/all?from={}&size={} - GET: getRequests({}, {})", from, size, from, size);
        log.debug("/requests/all from requesterId = {}", requesterId);

        return requestService.getAll(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ReqGetDto getRequestByRequestId(@RequestHeader("X-Sharer-User-Id") final Long requesterId,
                                           @PathVariable final Long requestId) {
        log.debug("/requests/{} - GET: getRequestByRequestId({}, {})", requestId, requesterId, requesterId);
        log.debug("/requests/{requestId} from requesterId = {}", requesterId);
        return requestService.getRequestDtoById(requestId, requesterId);
    }
}
