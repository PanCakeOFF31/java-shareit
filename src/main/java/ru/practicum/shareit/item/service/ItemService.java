package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item getItemById(final long itemId);

    Item getItemByIdAndOwnerId(final long itemId, final long ownerId);

    ItemResponseDto getItemDtoById(final long itemId, final long ownerId);

    List<Item> getItemsByRequestId(final long requestId);

    boolean containsItemById(final long itemId);

    void itemExists(final long itemId);

    boolean containsItemWithOwner(final long itemId, final long ownerId);

    void ownerOwnsItem(final long itemId, final long ownerId);

    ItemResponseDto createItem(final ItemRequestDto itemDto, final long ownerId);

    ItemResponseDto updateItem(final ItemRequestDto itemDto, final long ownerId, final long itemId);

    List<ItemResponseDto> getItemsByOwner(final long userId,
                                          final int from,
                                          final int size);

    List<ItemResponseDto> searchItems(final long userId, final String text,
                                      final int from,
                                      final int size);

    CommentResponseDto createComment(final CommentRequestDto commentDto, final long authorId, final long itemId);
}
