package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.Request;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    public static ReqCreateDto mapToReqCreateDto(final Request request) {
        return ReqCreateDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }


    public static ReqGetDto mapToReqGetDto(final Request request) {
        return ReqGetDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public static List<ReqGetDto> mapToReqGetDto(final Iterable<Request> requests) {
        List<ReqGetDto> dtos = new ArrayList<>();

        for (Request request : requests) {
            dtos.add(mapToReqGetDto(request));
        }

        return dtos;
    }

    public static Request mapToRequest(final ReqRequestDto requestDto) {
        return Request.builder()
                .description(requestDto.getDescription())
                .build();
    }
}
