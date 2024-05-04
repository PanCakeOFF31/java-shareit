package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemInMemoryRepo implements ItemRepo {
    private final Map<Long, Item> items = new HashMap<>(128);
    private long generatedId = 0;

    @Override
    public long addItem(final Item item) {
        log.info("ItemInMemoryRepository - service.addItem({})", item);

        long id = generateId();

        item.setId(id);
        items.put(id, item);

        return id;
    }

    @Override
    public Item updateItem(Item item) {
        log.info("ItemInMemoryRepository - service.updateItem({})", item);

        items.put(item.getId(), item);
        return item;
    }

    @Override

    public Optional<Item> findItem(long itemId) {
        log.info("ItemInMemoryRepository - service.findItem({})", itemId);

        Item item = items.get(itemId);

        if (item == null) {
            log.info("Предмет с id: {} не нашелся в репозитории", itemId);
            return Optional.empty();
        }

        log.info("Предмет с id: {} найден в репозитории", itemId);
        return Optional.of(new Item(item));
    }

    @Override
    public List<Item> findItemsByUser(long userId) {
        log.info("ItemInMemoryRepository - service.findItemsByUser({})", userId);

        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(long userId, String text) {
        log.info("ItemInMemoryRepository - service.searchItems({}, {})", userId, text);

        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsItemById(final long itemId) {
        log.info("ItemInMemoryRepository - service.containsItemById({})", itemId);
        return items.containsKey(itemId);
    }

    @Override
    public List<Item> getAllItems() {
        log.info("ItemInMemoryRepository - service.getAll()");
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Comment> getAllComments() {
        return null;
    }

    private long generateId() {
        return ++generatedId;
    }

}