package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ReqCreateDto;
import ru.practicum.shareit.request.dto.ReqGetDto;
import ru.practicum.shareit.request.dto.ReqRequestDto;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    Optional<Request> findRequestById(final long requestId);

    Request getRequestDtoById(final long requestId) throws RequestNotFoundException;

    boolean containsRequestById(final long requestId);

    void requestExists(final long requestId) throws RequestNotFoundException;

    ReqCreateDto createRequest(final ReqRequestDto reqRequestDto, final long requesterId);

    ReqGetDto getRequestDtoById(final long requestId, final long requesterId);

    List<ReqGetDto> getRequestsByRequester(final long requesterId, final int from, final int size);

    List<ReqGetDto> getAll(final long requesterId, final int from, final int size);
}
