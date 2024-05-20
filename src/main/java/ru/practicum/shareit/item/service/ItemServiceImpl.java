package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingOrderResponseDto;
import ru.practicum.shareit.booking.exception.UserNotBookedItemException;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.common.CommonValidation;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnerIncorrectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
public class ItemServiceImpl implements ItemService {
    private final UserService userService;

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;


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
    public List<Item> getItemsByRequestId(long requestId) {
        log.debug("ItemServiceImpl - service.getItemsByRequestId({})", requestId);
        return itemRepository.findAllByRequestId(requestId, Pageable.ofSize(100));
    }

    @Override
    public ItemResponseDto getItemDtoById(final long itemId, final long ownerId) {
        log.debug("ItemServiceImpl - service.getItemDtoById({}, {})", itemId, ownerId);

        final ItemResponseDto responseDto = ItemMapper.mapToItemResponseDto(getItemById(itemId));

        if (containsItemWithOwner(itemId, ownerId)) {
            responseDto.setNextBooking(findNearNextBookingIdByItem(itemId));

//            Если нет следующего, то за последний принимаю тот, который сейчас пересекается с NOW()
            if (responseDto.getNextBooking() == null)
                responseDto.setLastBooking(findNearOnlyLastBookingIdByItem(itemId));
            else
                responseDto.setLastBooking(findNearLastBookingIdByItem(itemId));
        }

        return responseDto;
    }

    public BookingOrderResponseDto findNearNextBookingIdByItem(long itemId) {
        log.debug("ItemServiceImpl - service.findNearNextBookingIdByItem({})", itemId);

        Optional<BookingOrderResponseDto> bookingItem = itemRepository
                .findTopBookingItemByItemIdAndStartGreaterThanEqualAndStatusInOrderByStartAsc(
                        itemId,
                        LocalDateTime.now(),
                        List.of(Status.WAITING, Status.APPROVED),
                        Pageable.ofSize(1)).stream()
                .findFirst();

        return bookingItem.orElse(null);
    }

    public BookingOrderResponseDto findNearLastBookingIdByItem(final long itemId) {
        log.debug("ItemServiceImpl - service.findNearLastBookingIdByItem({})", itemId);

        Optional<BookingOrderResponseDto> bookingItem = itemRepository
                .findTopBookingItemByItemIdAndEndLessThanEqualOrderByEndDesc(
                        itemId, LocalDateTime.now(), Pageable.ofSize(1)).stream()
                .findFirst();

        return bookingItem.orElse(null);
    }

    public BookingOrderResponseDto findNearOnlyLastBookingIdByItem(final long itemId) {
        log.debug("ItemServiceImpl - service.findNearOnlyLastBookingIdByItem({})", itemId);

        Optional<BookingOrderResponseDto> bookingItem = itemRepository
                .findTopBookingItemByItemIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByEndDesc(
                        itemId, LocalDateTime.now(), Pageable.ofSize(1)).stream()
                .findFirst();

        return bookingItem.orElse(null);
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

    @Transactional
    @Override
    public ItemResponseDto createItem(final ItemRequestDto itemDto, long ownerId) {
        log.debug("ItemServiceImpl - service.createItem({}, {})", itemDto, ownerId);

        final Item item = ItemMapper.mapToItem(itemDto);
        final User user = userService.getUserById(ownerId);

        item.setOwner(user);

        Long requestId = itemDto.getRequestId();

        if (requestId != null) {
            final Request request = requestRepository.findById(requestId)
                    .orElseThrow(RequestNotFoundException::new);
            item.setRequest(request);
        }

        return ItemMapper.mapToItemResponseDto(itemRepository.save(item));
    }

    @Transactional
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
    public List<ItemResponseDto> getItemsByOwner(final long ownerId,
                                                 final int from,
                                                 final int size) {
        log.debug("ItemServiceImpl - service.getItemsByUser({})", ownerId);

        CommonValidation.paginateValidation(from, size);
        userService.userExists(ownerId);

        List<ItemResponseDto> responses = ItemMapper
                .mapToItemResponseDto(itemRepository
                        .findAllByOwnerIdOrderByIdAsc(ownerId, PageRequest.of(from > 0 ? from / size : 0, size)));

        responses.forEach(item -> {
            final long itemId = item.getId();

            item.setNextBooking(findNearNextBookingIdByItem(itemId));

            if (item.getNextBooking() == null)
                item.setLastBooking(findNearOnlyLastBookingIdByItem(itemId));
            else
                item.setLastBooking(findNearLastBookingIdByItem(itemId));
        });

        return responses;
    }

    @Override
    public List<ItemResponseDto> searchItems(long userId, String text,
                                             final int from,
                                             final int size) {
        log.debug("ItemServiceImpl - service.searchItems({}, {})", userId, text);

        CommonValidation.paginateValidation(from, size);

        if (text.isBlank())
            return List.of();

        return ItemMapper.mapToItemResponseDto(itemRepository
                .findItemsByNameOrDescriptionTextAndIsAvailable(text.trim().toLowerCase(), from, size));
    }

    @Transactional
    @Override
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, long authorId, long itemId) {
        log.debug("ItemServiceImpl - service.createComment({}, {}, {})", commentRequestDto, authorId, itemId);

        final Item item = getItemById(itemId);
        final User user = userService.getUserById(authorId);

        userBookedItemWhenever(itemId, authorId);

        final Comment comment = CommentMapper.mapToComment(commentRequestDto);

        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        final Comment saveComment = commentRepository.save(comment);

        return CommentMapper.mapToCommentResponseDto(saveComment);
    }

    private void userBookedItemWhenever(final long itemId, final long bookerId) {
        log.debug("ItemServiceImpl - service.userBookedItem({}, {})", itemId, bookerId);

        if (!bookingRepository.existsBookingByItemIdAndBookerIdAndStatusIsNotAndEndLessThan(
                itemId, bookerId, Status.REJECTED, LocalDateTime.now()))
            throw new UserNotBookedItemException();
    }
}
