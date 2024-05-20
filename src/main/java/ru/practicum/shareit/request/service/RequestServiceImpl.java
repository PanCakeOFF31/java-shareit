package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.CommonValidation;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ReqCreateDto;
import ru.practicum.shareit.request.dto.ReqGetDto;
import ru.practicum.shareit.request.dto.ReqRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final ItemService itemService;

    private final RequestRepository requestRepository;
    private static final String NO_FOUND_REQUEST = "Такого запроса с id: %d не существует в хранилище";

    @Override
    public Optional<Request> findRequestById(final long requestId) {
        log.debug("RequestServiceImpl - service.findRequestById({})", requestId);
        return requestRepository.findById(requestId);
    }

    @Override
    public Request getRequestDtoById(long requestId) {
        log.debug("RequestServiceImpl - service.getRequestById({})", requestId);
        return findRequestById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(String.format(NO_FOUND_REQUEST, requestId)));
    }

    @Transactional
    @Override
    public ReqCreateDto createRequest(ReqRequestDto reqRequestDto, long requesterId) {
        log.debug("RequestServiceImpl - service.createRequest({}, {})", reqRequestDto, requesterId);

        final Request request = RequestMapper.mapToRequest(reqRequestDto);
        final User requester = userService.getUserById(requesterId);

        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        return RequestMapper.mapToReqCreateDto(requestRepository.save(request));
    }

    @Override
    public ReqGetDto getRequestDtoById(long requestId, long requesterId) {
        log.debug("RequestServiceImpl - service.getRequestByRequestId({}, {})", requestId, requesterId);

        userService.userExists(requesterId);

        Request request = getRequestDtoById(requestId);
        ReqGetDto reqGetDto = RequestMapper.mapToReqGetDto(request);

        List<ItemReqDto> itemReqs = ItemMapper.mapToItemReqDto(itemService.getItemsByRequestId(requestId));

        reqGetDto.setItems(itemReqs);

        return reqGetDto;
    }

    @Override
    public List<ReqGetDto> getRequestsByRequester(long requesterId, int from, int size) {
        log.debug("RequestServiceImpl - service.getRequestsByRequester({}, {}, {})", requesterId, from, size);

        CommonValidation.paginateValidation(from, size);
        userService.userExists(requesterId);

        List<ReqGetDto> reqs = RequestMapper.mapToReqGetDto(requestRepository
                .findAllByRequesterIdOrderByCreatedDesc(requesterId, PageRequest.of(from > 0 ? from / size : 0, size)));

        reqs.forEach(req -> req.setItems(ItemMapper.mapToItemReqDto(itemService.getItemsByRequestId(req.getId()))));

        return reqs;
    }

    @Override
    public List<ReqGetDto> getAll(long requesterId, int from, int size) {
        log.debug("RequestServiceImpl - service.getAll({}, {}, {})", requesterId, from, size);

        CommonValidation.paginateValidation(from, size);
        userService.userExists(requesterId);

        List<ReqGetDto> reqs = RequestMapper.mapToReqGetDto(requestRepository
                .findAllByRequesterIdNot(requesterId, PageRequest.of(from > 0 ? from / size : 0, size)));

        reqs.forEach(req -> req.setItems(ItemMapper.mapToItemReqDto(itemService.getItemsByRequestId(req.getId()))));

        return reqs;
    }
}