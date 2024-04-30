package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Optional<Item> findItemById(final long itemId);

    Item getItemById(final long itemId);

    Optional<Item> findItemByIdAndOwnerId(final long itemId, final long ownerId);

    Item getItemByIdAndOwnerId(final long itemId, final long ownerId);

    ItemResponseDto getItemDtoById(final long itemId, final long ownerId);

    ItemBookingDto getItemBookingDtoById(final long itemId, final long ownerId);

    boolean containsItemById(final long itemId);

    void itemExists(final long itemId);

    boolean containsItemWithOwner(final long itemId, final long ownerId);

    void ownerOwnsItem(final long itemId, final long ownerId);

    ItemRequestDto createItem(final ItemRequestDto itemDto, final long userId);

    ItemRequestDto updateItem(final ItemRequestDto itemDto, final long userId, final long itemId);

    List<ItemResponseDto> getItemsByOwner(final long userId);

    List<ItemRequestDto> searchItems(final long userId, final String text);

    List<ItemRequestDto> getAllItems();

    List<CommentResponseDto> getAllComments();

    CommentResponseDto createComment(final CommentRequestDto commentDto, final long owneId, final long itemId);
}
