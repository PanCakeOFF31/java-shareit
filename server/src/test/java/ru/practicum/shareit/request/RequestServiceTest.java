package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ReqCreateDto;
import ru.practicum.shareit.request.dto.ReqGetDto;
import ru.practicum.shareit.request.dto.ReqRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @InjectMocks
    private RequestServiceImpl requestService;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private RequestRepository requestRepository;

    private User expectedRequester;
    private ReqRequestDto reqRequestDto;
    private ReqCreateDto reqCreateDto;
    private ReqGetDto reqGetDto;
    private Request expectedRequest;

    private User expectedReqOwner;
    private Item expectedReqItem;

    private long anyRequesterId;
    private long anyRequestId;

    private long anyReqOwnerId;
    private long anyReqItemId;

    private LocalDateTime ldt;
    private int from;
    private int size;
    private Pageable pageable;


    @BeforeEach
    public void preTestInitialization() {
        anyRequesterId = 5L;
        anyRequestId = 888L;

        anyReqOwnerId = 77L;
        anyReqItemId = 2L;

        ldt = LocalDateTime.now().withNano(0);

        from = 0;
        size = 10;
        pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        expectedRequester = User.builder()
                .id(anyRequesterId)
                .name("requester Jingo")
                .email("jingo@yandex.gol")
                .build();

        reqRequestDto = ReqRequestDto.builder()
                .description("item request description")
                .build();

        reqCreateDto = ReqCreateDto.builder()
                .id(anyRequestId)
                .description(reqRequestDto.getDescription())
                .created(ldt)
                .build();

        expectedRequest = Request.builder()
                .id(anyRequestId)
                .description(reqRequestDto.getDescription())
                .created(ldt)
                .requester(expectedRequester)
                .build();

        expectedReqOwner = User.builder()
                .id(anyReqOwnerId)
                .name("maxim")
                .email("mak@yandex.ru")
                .build();

        expectedReqItem = Item.builder()
                .id(anyReqItemId)
                .name("item-name")
                .description("any-description")
                .available(true)
                .owner(expectedReqOwner)
                .comments(new ArrayList<>())
                .request(expectedRequest)
                .build();

        reqGetDto = ReqGetDto.builder()
                .id(anyRequestId)
                .description(reqCreateDto.getDescription())
                .created(ldt)
                .items(List.of(ItemMapper.mapToItemReqDto(expectedReqItem)))
                .build();
    }

    @Test
    public void test_T0010_PS01_createRequest() {
        Mockito.when(userService.getUserById(anyRequesterId))
                .thenReturn(expectedReqOwner);

        Mockito.when(requestRepository.save(any(Request.class)))
                .thenReturn(expectedRequest);

        ReqCreateDto createdRequest = requestService.createRequest(reqRequestDto, anyRequesterId);
        assertEquals(createdRequest, reqCreateDto);

        Mockito.verify(userService, Mockito.only()).getUserById(anyRequesterId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(requestRepository, Mockito.only()).save(any(Request.class));
        Mockito.verifyNoMoreInteractions(requestRepository);

        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    public void test_T0010_NS01_createRequest_noUserWithId() {
        Mockito.when(userService.getUserById(anyRequesterId))
                .thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> requestService.createRequest(reqRequestDto, anyRequesterId));

        Mockito.verify(userService, Mockito.only()).getUserById(anyRequesterId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verifyNoInteractions(itemService);
        Mockito.verifyNoInteractions(requestRepository);
    }

    @Test
    public void test_T0020_PS01_getRequestByRequester() {
        Mockito.doNothing().when(userService).userExists(anyRequesterId);

        Request request1 = new Request(expectedRequest);
        Request request2 = new Request(expectedRequest);
        List<Request> result = List.of(request1, request2);

        List<ReqGetDto> resultAsDto = RequestMapper.mapToReqGetDto(result);
        resultAsDto.forEach(req -> req.setItems(List.of(ItemMapper.mapToItemReqDto(expectedReqItem))));

        Mockito.when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(anyRequesterId, pageable))
                .thenReturn(result);

        Mockito.when(itemService.getItemsByRequestId(anyRequestId))
                .thenReturn(List.of(expectedReqItem));

        List<ReqGetDto> gotRequests = requestService.getRequestsByRequester(anyRequesterId, from, size);
        assertEquals(gotRequests, resultAsDto);

        Mockito.verify(userService, Mockito.only()).userExists(anyRequesterId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(requestRepository, Mockito.only()).findAllByRequesterIdOrderByCreatedDesc(anyRequesterId, pageable);
        Mockito.verifyNoMoreInteractions(requestRepository);

        Mockito.verify(itemService, Mockito.times(2)).getItemsByRequestId(anyRequestId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void test_T0020_NS01_getRequestByRequester_noUserWithId() {
        Mockito.doThrow(UserNotFoundException.class).when(userService).userExists(anyRequesterId);

        assertThrows(UserNotFoundException.class,
                () -> requestService.getRequestsByRequester(anyRequesterId, from, size));

        Mockito.verify(userService, Mockito.only()).userExists(anyRequesterId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(itemService);
    }


    @Test
    public void test_T0030_PS01_getAll() {
        Mockito.doNothing().when(userService).userExists(anyRequesterId);

        Request request1 = new Request(expectedRequest);
        Request request2 = new Request(expectedRequest);
        List<Request> result = List.of(request1, request2);

        List<ReqGetDto> resultAsDto = RequestMapper.mapToReqGetDto(result);
        resultAsDto.forEach(req -> req.setItems(List.of(ItemMapper.mapToItemReqDto(expectedReqItem))));

        Mockito.when(requestRepository.findAllByRequesterIdNot(anyRequesterId, pageable))
                .thenReturn(result);

        Mockito.when(itemService.getItemsByRequestId(anyRequestId))
                .thenReturn(List.of(expectedReqItem));

        List<ReqGetDto> gotRequests = requestService.getAll(anyRequesterId, from, size);
        assertEquals(gotRequests, resultAsDto);

        Mockito.verify(userService, Mockito.only()).userExists(anyRequesterId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(requestRepository, Mockito.only()).findAllByRequesterIdNot(anyRequesterId, pageable);
        Mockito.verifyNoMoreInteractions(requestRepository);

        Mockito.verify(itemService, Mockito.times(2)).getItemsByRequestId(anyRequestId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void test_T0030_NS01_getAll_noUserWithId() {
        Mockito.doThrow(UserNotFoundException.class).when(userService).userExists(anyRequesterId);

        assertThrows(UserNotFoundException.class,
                () -> requestService.getAll(anyRequesterId, from, size));

        Mockito.verify(userService, Mockito.only()).userExists(anyRequesterId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    public void test_T0040_PS01_getRequestDtoById() {
        Mockito.doNothing().when(userService).userExists(anyRequesterId);

        Mockito.when(requestRepository.findById(anyRequestId))
                .thenReturn(Optional.of(expectedRequest));

        Mockito.when(itemService.getItemsByRequestId(anyRequestId))
                .thenReturn(List.of(expectedReqItem));

        ReqGetDto gotRequest = requestService.getRequestDtoById(anyRequestId, anyRequesterId);
        assertEquals(gotRequest, reqGetDto);

        Mockito.verify(userService, Mockito.only()).userExists(anyRequesterId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(requestRepository, Mockito.only()).findById(anyRequestId);
        Mockito.verifyNoMoreInteractions(requestRepository);

        Mockito.verify(itemService, Mockito.only()).getItemsByRequestId(anyRequestId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void test_T0040_NS01_getRequestDtoById_noUserWithId() {
        Mockito.doThrow(UserNotFoundException.class).when(userService).userExists(anyRequesterId);

        assertThrows(UserNotFoundException.class,
                () -> requestService.getRequestDtoById(anyRequestId, anyRequesterId));

        Mockito.verify(userService, Mockito.only()).userExists(anyRequesterId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    public void test_T0040_NS01_getRequestDtoById_noRequestWithId() {
        Mockito.doNothing().when(userService).userExists(anyRequesterId);
        Mockito.doThrow(RequestNotFoundException.class).when(requestRepository).findById(anyRequestId);

        assertThrows(RequestNotFoundException.class,
                () -> requestService.getRequestDtoById(anyRequestId, anyRequesterId));

        Mockito.verify(userService, Mockito.only()).userExists(anyRequesterId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(requestRepository, Mockito.only()).findById(anyRequestId);
        Mockito.verifyNoMoreInteractions(requestRepository);

        Mockito.verifyNoInteractions(itemService);
    }
}