package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.exception.UserNotBookedItemException;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnerIncorrectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;

    private ItemRequestDto itemRequestDto;
    private ItemResponseDto itemResponseDto;
    private User expectedOwner;
    private Item expectedItem;

    private User expectedRequester;
    private Request expectedRequest;
    private User expectedReqOwner;
    private Item expectedReqItem;
    private ItemRequestDto itemReqRequestDto;
    private ItemResponseDto itemReqResponseDto;

    private User expectedAuthor;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private Comment expetctedComment;

    private long anyOwnerId;
    private long anyItemId;

    private long anyReqItemId;
    private long anyRequesterId;
    private long anyRequestId;
    private long anyReqOwnerId;

    private long anyAuthorId;
    private long anyCommentId;

    private LocalDateTime ldt;
    private int from;
    private int size;
    private Pageable pageable;
    private String text;

    @BeforeEach
    public void preTestInitialization() {
        anyOwnerId = 99L;
        anyItemId = 111L;

        anyRequesterId = 5L;
        anyRequestId = 8L;
        anyReqOwnerId = 77L;
        anyReqItemId = 3;

        anyAuthorId = 88L;
        anyCommentId = 17L;

        ldt = LocalDateTime.now().withNano(0);
        from = 0;
        size = 10;
        pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        text = "some text";

//        Item without Request
        expectedOwner = User.builder()
                .id(anyOwnerId)
                .name("maxim")
                .email("mak@yandex.ru")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .name("clock")
                .description("random description")
                .available(true)
                .requestId(null)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(anyItemId)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .requestId(null)
                .build();

        expectedItem = Item.builder()
                .id(anyItemId)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .owner(expectedOwner)
                .comments(new ArrayList<>())
                .request(null)
                .build();

//        Item by Request
        expectedRequester = User.builder()
                .id(anyRequesterId)
                .name("any name")
                .email("asdnk@yandex.com")
                .build();

        expectedRequest = Request.builder()
                .id(anyRequestId)
                .description("any request description")
                .created(ldt)
                .requester(expectedRequester)
                .build();

        itemReqRequestDto = ItemRequestDto.builder()
                .name("blue car")
                .description("random description")
                .available(true)
                .requestId(expectedRequest.getId())
                .build();

        itemReqResponseDto = ItemResponseDto.builder()
                .id(anyReqItemId)
                .name(itemReqRequestDto.getName())
                .description(itemReqRequestDto.getDescription())
                .available(itemReqRequestDto.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .requestId(expectedRequest.getId())
                .build();

        expectedReqOwner = User.builder()
                .id(anyReqOwnerId)
                .name("Boris")
                .email("bory@yandex.ru")
                .build();

        expectedReqItem = Item.builder()
                .id(anyReqItemId)
                .name(itemReqRequestDto.getName())
                .description(itemReqRequestDto.getDescription())
                .available(itemReqRequestDto.getAvailable())
                .owner(expectedReqOwner)
                .comments(new ArrayList<>())
                .request(expectedRequest)
                .build();

//        Comment for Item without Request
        expectedAuthor = User.builder()
                .id(anyAuthorId)
                .name("Nickola")
                .email("nick@yandex.ru")
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .text("some text info")
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(anyCommentId)
                .text(commentRequestDto.getText())
                .authorName(expectedAuthor.getName())
                .created(ldt.plusDays(1))
                .build();

        expetctedComment = Comment.builder()
                .id(anyCommentId)
                .text(commentRequestDto.getText())
                .created(commentResponseDto.getCreated())
                .item(expectedItem)
                .author(expectedAuthor)
                .build();

    }

    @Test
    public void test_T0010_PS01_createItem() {
        Mockito.when(userService.getUserById(anyOwnerId))
                .thenReturn(expectedOwner);

        Mockito.when(itemRepository.save(any(Item.class)))
                .thenReturn(expectedItem);

        ItemResponseDto createdItem = itemService.createItem(itemRequestDto, anyOwnerId);
        assertEquals(createdItem, itemResponseDto);

        Mockito.verify(userService, Mockito.only()).getUserById(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(itemRepository, Mockito.only()).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0010_PS02_createItem_withRequest() {
        Mockito.when(userService.getUserById(anyReqOwnerId))
                .thenReturn(expectedReqOwner);

        Mockito.when(requestRepository.findById(anyRequestId))
                .thenReturn(Optional.of(expectedRequest));

        Mockito.when(itemRepository.save(any(Item.class)))
                .thenReturn(expectedReqItem);

        ItemResponseDto createdItem = itemService.createItem(itemReqRequestDto, anyReqOwnerId);
        assertEquals(createdItem, itemReqResponseDto);

        Mockito.verify(userService, Mockito.only()).getUserById(anyReqOwnerId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(requestRepository, Mockito.only()).findById(anyRequestId);
        Mockito.verifyNoMoreInteractions(requestRepository);

        Mockito.verify(itemRepository, Mockito.only()).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0010_NS01_createItem_noUserWithId() {
        Mockito.when(userService.getUserById(anyOwnerId))
                .thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> itemService.createItem(itemRequestDto, anyOwnerId));

        Mockito.verify(userService, Mockito.only()).getUserById(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verifyNoInteractions(itemRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0010_NS02_createItem_noRequestWithId() {
        Mockito.when(userService.getUserById(anyReqOwnerId))
                .thenReturn(expectedReqOwner);

        Mockito.when(requestRepository.findById(anyRequestId))
                .thenThrow(RequestNotFoundException.class);

        assertThrows(RequestNotFoundException.class, () -> itemService.createItem(itemReqRequestDto, anyReqOwnerId));

        Mockito.verify(userService, Mockito.only()).getUserById(anyReqOwnerId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(requestRepository, Mockito.only()).findById(anyRequestId);
        Mockito.verifyNoMoreInteractions(requestRepository);

        Mockito.verifyNoInteractions(itemRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0020_PS01_updateItem() {
        Mockito.when(itemRepository.existsById(anyItemId))
                .thenReturn(true);

        Mockito.when(itemRepository.findItemByIdAndOwnerId(anyItemId, anyOwnerId))
                .thenReturn(Optional.of(expectedItem));

        Mockito.when(itemRepository.save(any(Item.class)))
                .thenReturn(expectedItem);

        ItemResponseDto updatedItem = itemService.updateItem(itemRequestDto, anyOwnerId, anyItemId);
        assertEquals(updatedItem, itemResponseDto);

        Mockito.verify(itemRepository, Mockito.times(1)).existsById(anyItemId);
        Mockito.verify(itemRepository, Mockito.times(1)).findItemByIdAndOwnerId(anyItemId, anyOwnerId);
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0020_PS02_updateItem_emptyFields() {
        Mockito.when(itemRepository.existsById(anyItemId))
                .thenReturn(true);

        Mockito.when(itemRepository.findItemByIdAndOwnerId(anyItemId, anyOwnerId))
                .thenReturn(Optional.of(expectedItem));

        ItemResponseDto updatedItem = itemService.updateItem(new ItemRequestDto(), anyOwnerId, anyItemId);
        assertEquals(updatedItem, itemResponseDto);

        Mockito.verify(itemRepository, Mockito.times(1)).existsById(anyItemId);
        Mockito.verify(itemRepository, Mockito.times(1)).findItemByIdAndOwnerId(anyItemId, anyOwnerId);
        Mockito.verify(itemRepository, Mockito.never()).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0020_NS01_updateItem_noItemWithId() {
        Mockito.when(itemRepository.existsById(anyItemId))
                .thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(itemRequestDto, anyOwnerId, anyItemId));

        Mockito.verify(itemRepository, Mockito.times(1)).existsById(anyItemId);
        Mockito.verify(itemRepository, Mockito.never()).findItemByIdAndOwnerId(anyItemId, anyOwnerId);
        Mockito.verify(itemRepository, Mockito.never()).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0020_NS02_updateItem_notItemOwner() {
        Mockito.when(itemRepository.existsById(anyItemId))
                .thenReturn(true);

        Mockito.when(itemRepository.findItemByIdAndOwnerId(anyItemId, anyOwnerId))
                .thenReturn(Optional.empty());

        assertThrows(ItemOwnerIncorrectException.class, () -> itemService.updateItem(itemRequestDto, anyOwnerId, anyItemId));

        Mockito.verify(itemRepository, Mockito.times(1)).existsById(anyItemId);
        Mockito.verify(itemRepository, Mockito.times(1)).findItemByIdAndOwnerId(anyItemId, anyOwnerId);
        Mockito.verify(itemRepository, Mockito.never()).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0030_PS01_getItemById_notItemOwner() {
        Mockito.when(itemRepository.findById(anyItemId))
                .thenReturn(Optional.of(expectedItem));

        Mockito.when(itemRepository.existsItemByIdAndOwnerId(anyItemId, anyOwnerId))
                .thenReturn(false);

        ItemResponseDto gotItem = itemService.getItemDtoById(anyItemId, anyOwnerId);
        assertEquals(gotItem, itemResponseDto);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(anyItemId);
        Mockito.verify(itemRepository, Mockito.times(1)).existsItemByIdAndOwnerId(anyItemId, anyOwnerId);
        Mockito.verify(itemRepository, Mockito.never())
                .findTopBookingItemByItemIdAndStartGreaterThanEqualAndStatusInOrderByStartAsc(anyLong(), any(LocalDateTime.class), any(List.class), any(Pageable.class));
        Mockito.verify(itemRepository, Mockito.never())
                .findTopBookingItemByItemIdAndEndLessThanEqualOrderByEndDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        Mockito.verify(itemRepository, Mockito.never())
                .findTopBookingItemByItemIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByEndDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0030_NS01_getItemById_noItemWithId() {
        Mockito.when(itemRepository.findById(anyItemId))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItemDtoById(anyItemId, anyOwnerId));

        Mockito.verify(itemRepository, Mockito.only()).findById(anyItemId);
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0040_PS01_getItemsByOwner() {
        Mockito.doNothing().when(userService).userExists(anyOwnerId);

        Item item1 = new Item(expectedItem);
        Item item2 = new Item(expectedItem);

        List<Item> result = List.of(item1, item2);
        List<ItemResponseDto> resultAsDto = ItemMapper.mapToItemResponseDto(result);

        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyOwnerId, pageable))
                .thenReturn(result);

        Mockito.when(itemRepository.findTopBookingItemByItemIdAndStartGreaterThanEqualAndStatusInOrderByStartAsc(anyLong(), any(LocalDateTime.class), any(List.class), any(Pageable.class)))
                .thenReturn(List.of());

        Mockito.when(itemRepository.findTopBookingItemByItemIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByEndDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of());

        List<ItemResponseDto> gotItems = itemService.getItemsByOwner(anyOwnerId, from, size);
        assertEquals(gotItems, resultAsDto);

        Mockito.verify(userService, Mockito.only()).userExists(anyOwnerId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(itemRepository, Mockito.times(1)).findAllByOwnerIdOrderByIdAsc(anyOwnerId, pageable);
        Mockito.verify(itemRepository, Mockito.times(2))
                .findTopBookingItemByItemIdAndStartGreaterThanEqualAndStatusInOrderByStartAsc(anyLong(), any(LocalDateTime.class), any(List.class), any(Pageable.class));
        Mockito.verify(itemRepository, Mockito.times(2))
                .findTopBookingItemByItemIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByEndDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        Mockito.verify(itemRepository, Mockito.times(0))
                .findTopBookingItemByItemIdAndEndLessThanEqualOrderByEndDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0050_PS01_searchItems() {
        Item item1 = new Item(expectedItem);
        Item item2 = new Item(expectedItem);

        List<Item> result = List.of(item1, item2);
        List<ItemResponseDto> resultAsDto = ItemMapper.mapToItemResponseDto(result);

        Mockito.when(itemRepository.findItemsByNameOrDescriptionTextAndIsAvailable(text, from, size))
                .thenReturn(result);

        List<ItemResponseDto> gotItems = itemService.searchItems(anyOwnerId, text, from, size);
        assertEquals(gotItems, resultAsDto);

        Mockito.verify(itemRepository, Mockito.only()).findItemsByNameOrDescriptionTextAndIsAvailable(text, from, size);
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0050_PS02_searchItems_emptyText() {
        List<ItemResponseDto> gotItems = itemService.searchItems(anyOwnerId, "", from, size);
        assertTrue(gotItems.isEmpty());

        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(itemRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0060_PS01_createComment() {
        Mockito.when(itemRepository.findById(anyItemId))
                .thenReturn(Optional.of(expectedItem));

        Mockito.when(userService.getUserById(anyAuthorId))
                .thenReturn(expectedAuthor);

        Mockito.when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusIsNotAndEndLessThan(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(true);

        Mockito.when(commentRepository.save(any(Comment.class)))
                .thenReturn(expetctedComment);

        CommentResponseDto createdComment = itemService.createComment(commentRequestDto, anyAuthorId, anyItemId);
        assertEquals(createdComment, commentResponseDto);

        Mockito.verify(itemRepository, Mockito.only()).findById(anyItemId);
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verify(userService, Mockito.only()).getUserById(anyAuthorId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(bookingRepository, Mockito.only()).existsBookingByItemIdAndBookerIdAndStatusIsNotAndEndLessThan(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class));
        Mockito.verifyNoMoreInteractions(bookingRepository);

        Mockito.verify(commentRepository, Mockito.only()).save(any(Comment.class));
        Mockito.verifyNoMoreInteractions(commentRepository);

        Mockito.verifyNoInteractions(requestRepository);
    }

    @Test
    public void test_T0060_NS01_createComment_noItemWithId() {
        Mockito.when(itemRepository.findById(anyItemId))
                .thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> itemService.createComment(commentRequestDto, anyAuthorId, anyItemId));

        Mockito.verify(itemRepository, Mockito.only()).findById(anyItemId);
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verifyNoInteractions(userService);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0060_NS02_createComment_noUserWithId() {
        Mockito.when(itemRepository.findById(anyItemId))
                .thenReturn(Optional.of(expectedItem));

        Mockito.when(userService.getUserById(anyAuthorId))
                .thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> itemService.createComment(commentRequestDto, anyAuthorId, anyItemId));

        Mockito.verify(itemRepository, Mockito.only()).findById(anyItemId);
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verify(userService, Mockito.only()).getUserById(anyAuthorId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    public void test_T0060_NS03_createComment_neverBookedItem() {
        Mockito.when(itemRepository.findById(anyItemId))
                .thenReturn(Optional.of(expectedItem));

        Mockito.when(userService.getUserById(anyAuthorId))
                .thenReturn(expectedAuthor);

        Mockito.when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusIsNotAndEndLessThan(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(UserNotBookedItemException.class, () -> itemService.createComment(commentRequestDto, anyAuthorId, anyItemId));
        Mockito.verify(itemRepository, Mockito.only()).findById(anyItemId);
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verify(userService, Mockito.only()).getUserById(anyAuthorId);
        Mockito.verifyNoMoreInteractions(userService);

        Mockito.verify(bookingRepository, Mockito.only()).existsBookingByItemIdAndBookerIdAndStatusIsNotAndEndLessThan(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class));
        Mockito.verifyNoMoreInteractions(bookingRepository);

        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(requestRepository);
    }
}