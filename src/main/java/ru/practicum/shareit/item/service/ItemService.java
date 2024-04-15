package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(final Item item, final long userId);

    Item updateItem(final ItemDto item, final long userId, final long itemId);

    Item getItem(final long userId, final long itemId);

    List<ItemDto> getItemsByUser(final long userId);

    List<ItemDto> searchItems(final long userId, final String text);

    List<ItemDto> getAll();

    void itemIsExist(final long itemId);

    boolean containsItemById(final long itemId);

}
