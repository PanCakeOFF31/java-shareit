package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    long addItem(final Item item);

    Item updateItem(final Item item);

    Optional<Item> findItem(final long itemId);

    List<Item> findItemsByUser(final long userId);

    List<Item> searchItems(final long userId, final String text);

    boolean containsItemById(final long itemId);

    //    TODO: удалить служебный метод
    List<Item> getAll();
}
