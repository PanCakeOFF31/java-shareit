package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingItemOrderDto;
import ru.practicum.shareit.booking.exception.UserNotBookedItemException;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnerIncorrectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private static final String NO_FOUND_ITEM = "Такого предмета с id: %d не существует в хранилище";
    private static final String INCORRECT_OWNER = "Пользователь с id: %d не является владельцем предмета с id: %d ";

    @Override
    public Optional<Item> findItemById(final long itemId) {
        log.debug("ItemServiceImpl - service.findItemById({})", itemId);
        return itemRepository.findById(itemId);
    }

    @Override
    public Item getItemById(long itemId) {
        log.debug("ItemServiceImpl - service.getItemById({})", itemId);
        return findItemById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format(NO_FOUND_ITEM, itemId)));
    }

    @Override
    public Optional<Item> findItemByIdAndOwnerId(final long itemId, final long ownerId) {
        log.debug("ItemServiceImpl - service.findItemByIdAndOwnerId({}, {})", ownerId, itemId);
        return itemRepository.findItemByIdAndOwnerId(itemId, ownerId);
    }


    @Override
    public Item getItemByIdAndOwnerId(final long itemId, final long ownerId) {
        log.debug("ItemServiceImpl - service.getItemByIdAndOwnerId({}, {})", itemId, ownerId);

        itemExists(itemId);

        return findItemByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new ItemOwnerIncorrectException(String.format(INCORRECT_OWNER, itemId, ownerId)));
    }

    @Override
    public ItemResponseDto getItemDtoById(final long itemId, final long ownerId) {
        log.debug("ItemServiceImpl - service.getItemDtoById({}, {})", itemId, ownerId);

        final ItemResponseDto responseDto = ItemMapper.mapToItemResponseDto(getItemById(itemId));

        if (containsItemWithOwner(itemId, ownerId)) {
            responseDto.setNextBooking(findNearNextBookingIdByItem(itemId));

//            Если нет следующего, то за последний принимаю тот, который сейчас пересекается с NOW()
            if (responseDto.getNextBooking() != null)
                responseDto.setLastBooking(findNearLastBookingIdByItem(itemId));
            else
                responseDto.setLastBooking(findNearOnlyLastBookingIdByItem(itemId));
        }

        return responseDto;
    }

    public BookingItemOrderDto findNearLastBookingIdByItem(final long itemId) {
        log.debug("ItemServiceImpl - service.findNearLastBookingIdByItem({})", itemId);

        Optional<BookingItemOrderDto> bookingItem = itemRepository
                .findTopBookingItemByItemIdAndEndLessThanEqualOrderByEndDesc(
                        itemId, LocalDateTime.now(), Pageable.ofSize(1)).stream()
                .findFirst();

        return bookingItem.orElse(null);

    }

    public BookingItemOrderDto findNearOnlyLastBookingIdByItem(final long itemId) {
        log.debug("ItemServiceImpl - service.findNearOnlyLastBookingIdByItem({})", itemId);

        Optional<BookingItemOrderDto> bookingItem = itemRepository
                .findTopBookingItemByItemIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByEndDesc(
                        itemId, LocalDateTime.now(), Pageable.ofSize(1)).stream()
                .findFirst();

        return bookingItem.orElse(null);
    }

    public BookingItemOrderDto findNearNextBookingIdByItem(long itemId) {
        log.debug("ItemServiceImpl - service.findNearNextBookingIdByItem({})", itemId);

        Optional<BookingItemOrderDto> bookingItem = itemRepository
                .findTopBookingItemByItemIdAndStartGreaterThanEqualAndStatusInOrderByStartAsc(
                        itemId,
                        LocalDateTime.now(),
                        List.of(Status.WAITING, Status.APPROVED),
                        Pageable.ofSize(1)).stream()
                .findFirst();

        return bookingItem.orElse(null);
    }

    @Override
    public ItemBookingDto getItemBookingDtoById(final long ownerId, final long itemId) {
        log.debug("ItemServiceImpl - service.getItemBookingDtoById({}, {})", ownerId, itemId);
        return ItemMapper.mapToItemBookingDto(getItemById(itemId));
    }

    @Override
    public boolean containsItemById(final long itemId) {
        log.debug("ItemServiceImpl - service.containsItemById()");
        return itemRepository.existsById(itemId);
    }

    @Override
    public void itemExists(final long itemId) {
        log.debug("ItemServiceImpl - service.itemExists({})", itemId);

        if (!containsItemById(itemId)) {
            String message = String.format(NO_FOUND_ITEM, itemId);
            log.warn(message);
            throw new ItemNotFoundException(message);
        }
    }

    @Override
    public boolean containsItemWithOwner(final long itemId, final long ownerId) {
        log.debug("ItemServiceImpl - service.containsItemWithOwner({}, {})", itemId, ownerId);
        return itemRepository.existsItemByIdAndOwnerId(itemId, ownerId);
    }

    public void ownerOwnsItem(final long itemId, final long ownerId) {
        log.debug("BookingServiceImpl - service.itemOwnerValidation({}, {})", ownerId, itemId);

        if (!containsItemWithOwner(itemId, ownerId)) {
            String message =
                    String.format("Пользователь c id %d  - не является владельцем вещи id %d", itemId, ownerId);
            log.warn(message);
            throw new ItemOwnerIncorrectException(message);
        }
    }

    @Override
    public ItemResponseDto createItem(final ItemRequestDto itemDto, long userId) {
        log.debug("ItemServiceImpl - service.createItem({}, {})", itemDto, userId);

        final Item item = ItemMapper.mapToItem(itemDto);
        final User user = userService.getUserById(userId);
        item.setOwner(user);

        return ItemMapper.mapToItemResponseDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto updateItem(ItemRequestDto itemDto, long ownerId, long itemId) {
        log.debug("ItemServiceImpl - service.updateItem({}, {}, {})", itemDto, ownerId, itemId);

        final Item gotItem = getItemByIdAndOwnerId(itemId, ownerId);

        String providedName = itemDto.getName();
        String providedDescription = itemDto.getDescription();
        Boolean providedAvailable = itemDto.getAvailable();

        if (providedName == null && providedDescription == null && providedAvailable == null) {
            log.info("Прислан объект Item без обновляемых полей. Никакого обновления не произошло");
            return ItemMapper.mapToItemResponseDto(gotItem);
        }

        if (providedName != null)
            gotItem.setName(providedName);

        if (providedDescription != null)
            gotItem.setDescription(providedDescription);

        if (providedAvailable != null)
            gotItem.setAvailable(providedAvailable);

        return ItemMapper.mapToItemResponseDto(itemRepository.save(gotItem));
    }


    @Override
    public List<ItemResponseDto> getItemsByOwner(final long ownerId) {
        log.debug("ItemServiceImpl - service.getItemsByUser({})", ownerId);

        userService.userExists(ownerId);

        List<ItemResponseDto> responses = ItemMapper.mapToItemResponseDto(itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId, Pageable.ofSize(100)));

        responses.forEach(item -> {
            item.setNextBooking(findNearNextBookingIdByItem(item.getId()));
            item.setLastBooking(findNearLastBookingIdByItem(item.getId()));
        });

        return responses;
    }

    @Override
    public List<ItemResponseDto> searchItems(long userId, String text) {
        log.debug("ItemServiceImpl - service.searchItems({}, {})", userId, text);

        if (text.isBlank())
            return List.of();

        return ItemMapper.mapToItemResponseDto(itemRepository
                .findItemsByNameOrDescriptionText(text.trim().toLowerCase())
        );
    }

    @Override
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, long userId, long itemId) {
        log.debug("ItemServiceImpl - service.createComment({}, {}, {})", commentRequestDto, userId, itemId);

        final Item item = getItemById(itemId);
        final User user = userService.getUserById(userId);

        userBookedWheneverItem(itemId, userId);

        final Comment comment = CommentMapper.mapToComment(commentRequestDto);

        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        final Comment saveComment = commentRepository.save(comment);

        return CommentMapper.mapToCommentResponseDto(saveComment);
    }

    private void userBookedWheneverItem(final long itemId, final long bookerId) {
        log.debug("ItemServiceImpl - service.userBookedItem({}, {})", itemId, bookerId);

        if (!bookingRepository.existsBookingByItemIdAndBookerIdAndStatusIsNotAndEndLessThan(
                itemId, bookerId, Status.REJECTED, LocalDateTime.now()))
            throw new UserNotBookedItemException();
    }

    //    TODO: служебный метод
    @Override
    public List<ItemResponseDto> getAllItems() {
        log.debug("ItemServiceImpl - service.getAllItems()");
        return itemRepository.findAll(Pageable.ofSize(100)).stream()
                .map(ItemMapper::mapToItemResponseDto)
                .collect(Collectors.toList());
    }

    //    TODO: служебный метод
    @Override
    public List<CommentResponseDto> getAllComments() {
        log.debug("ItemServiceImpl - service.getAllComments()");
        return itemRepository.findAll(Pageable.ofSize(100)).stream()
                .map(Item::getComments)
                .flatMap(Collection::stream)
                .map(CommentMapper::mapToCommentResponseDto)
                .collect(Collectors.toList());
    }

//    private void emptyFieldValidation(final Item item) {
//        log.debug("ItemServiceImpl - service.emptyFieldValidation({})", item);
//
//        String name = item.getName();
//        String description = item.getDescription();
//        Boolean available = item.getAvailable();
//
//        if (name == null || description == null || available == null
//                || name.isBlank() || description.isBlank()) {
//            String message = "Отсутствует часть обязательны полей name/description/available - " + item;
//            log.warn(message);
//            throw new ItemFieldValidationException(message);
//        }
//    }
}
