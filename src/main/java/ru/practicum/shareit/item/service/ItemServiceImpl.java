package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnerIncorrectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private static final String NO_FOUND_ITEM = "Такого предмета с id: %d не существует в хранилище";
    private static final String INCORRECT_OWNER = "Пользователь с id: %d не является владельцем предмета с id: %d ";

    @Override
    public Item createItem(Item item, long userId) {
        log.info("ItemServiceImpl - service.createItem({}, {})", item, userId);

        userService.userIsExist(userId);
        item.setOwner(userId);

        long assignedId = itemRepository.addItem(item);
        item.setId(assignedId);

        return item;
    }

    @Override
    public Item updateItem(ItemDto item, long userId, long itemId) {
        log.info("ItemServiceImpl - service.updateItem({}, {}, {})", item, userId, itemId);

        Item gettedItem = getItem(userId, itemId);
        Item providedItem = ItemMapper.toItem(userId, item);

        if (gettedItem.getOwner() != userId) {
            String message = String.format(INCORRECT_OWNER, userId, itemId);
            throw new ItemOwnerIncorrectException(message);
        }

        String providedName = providedItem.getName();
        String providedDescription = providedItem.getDescription();
        Boolean providedAvailable = providedItem.getAvailable();

        if (providedName == null && providedDescription == null && providedAvailable == null) {
            log.info("Прислан объект Item без обновляемых полей. Никакого обновления не произошло");
            return gettedItem;
        }

        if (providedName != null) gettedItem.setName(providedName);

        if (providedDescription != null) gettedItem.setDescription(providedDescription);

        if (providedAvailable != null) gettedItem.setAvailable(providedAvailable);

        return itemRepository.updateItem(gettedItem);
    }

    @Override
    public Item getItem(long userId, long itemId) {
        log.info("ItemServiceImpl - service.getItem({}, {})", userId, itemId);

        userService.userIsExist(userId);

        String message = String.format(NO_FOUND_ITEM, itemId);
        return itemRepository.findItem(itemId).orElseThrow(() -> new ItemNotFoundException(message));
    }

    @Override
    public List<ItemDto> getItemsByUser(final long userId) {
        log.info("ItemServiceImpl - service.getItemsByUser({})", userId);

        userService.userIsExist(userId);
        return itemRepository.findItemsByUser(userId).stream().map(ItemMapper::tiItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        log.info("ItemServiceImpl - service.searchItems({}, {})", userId, text);

        if (text.isBlank()) return List.of();

        userService.userIsExist(userId);
        return itemRepository.searchItems(userId, text.trim().toLowerCase()).stream().map(ItemMapper::tiItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAll() {
        log.info("ItemServiceImpl - service.getAll()");

        return itemRepository.getAll().stream().map(ItemMapper::tiItemDto).collect(Collectors.toList());
    }

    @Override
    public boolean containsItemById(final long itemId) {
        log.info("ItemServiceImpl - service.containsItemById()");
        return itemRepository.containsItemById(itemId);
    }

    @Override
    public void itemIsExist(final long itemId) {
        log.info("ItemServiceImpl - service.itemIsExist()");

        if (!containsItemById(itemId)) {
            String message = String.format(NO_FOUND_ITEM, itemId);
            log.warn(message);
            throw new ItemNotFoundException(message);
        }
    }
}
