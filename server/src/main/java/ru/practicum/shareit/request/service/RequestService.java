package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ReqCreateDto;
import ru.practicum.shareit.request.dto.ReqGetDto;
import ru.practicum.shareit.request.dto.ReqRequestDto;

import java.util.List;

public interface RequestService {
    ReqCreateDto createRequest(final ReqRequestDto reqRequestDto, final long requesterId);

    ReqGetDto getRequestDtoById(final long requestId, final long requesterId);

    List<ReqGetDto> getRequestsByRequester(final long requesterId, final int from, final int size);

    List<ReqGetDto> getAll(final long requesterId, final int from, final int size);
}
