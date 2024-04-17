package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemFieldValidationException;
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
    public ItemDto createItem(final ItemDto itemDto, long userId) {
        log.info("ItemServiceImpl - service.createItem({}, {})", itemDto, userId);

        userService.userIsExist(userId);

        final Item item = ItemMapper.toItem(itemDto, userId);
        emptyFieldValidation(item);

        long assignedId = itemRepository.addItem(item);
        item.setId(assignedId);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) {
        log.info("ItemServiceImpl - service.updateItem({}, {}, {})", itemDto, userId, itemId);

        final Item gotItem = getItem(userId, itemId);

        if (gotItem.getOwner() != userId) {
            String message = String.format(INCORRECT_OWNER, userId, itemId);
            throw new ItemOwnerIncorrectException(message);
        }

        String providedName = itemDto.getName();
        String providedDescription = itemDto.getDescription();
        Boolean providedAvailable = itemDto.getAvailable();

        if (providedName == null && providedDescription == null && providedAvailable == null) {
            log.info("Прислан объект Item без обновляемых полей. Никакого обновления не произошло");
            return ItemMapper.toItemDto(gotItem);
        }


        if (providedName != null)
            gotItem.setName(providedName);

        if (providedDescription != null)

            gotItem.setDescription(providedDescription);

        if (providedAvailable != null)
            gotItem.setAvailable(providedAvailable);

        return ItemMapper.toItemDto(itemRepository.updateItem(gotItem));
    }

    @Override
    public ItemDto getItemDto(long userId, long itemId) {
        log.info("ItemServiceImpl - service.getItemDto({}, {})", userId, itemId);
        return ItemMapper.toItemDto(getItem(userId, itemId));
    }

    @Override
    public Item getItem(long userId, long itemId) {
        log.info("ItemServiceImpl - service.getItem({}, {})", userId, itemId);

        userService.userIsExist(userId);

        String message = String.format(NO_FOUND_ITEM, itemId);
        return itemRepository
                .findItem(itemId)
                .orElseThrow(() -> new ItemNotFoundException(message));
    }

    @Override
    public List<ItemDto> getItemsByUser(final long userId) {
        log.info("ItemServiceImpl - service.getItemsByUser({})", userId);

        userService.userIsExist(userId);
        return itemRepository.findItemsByUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        log.info("ItemServiceImpl - service.searchItems({}, {})", userId, text);

        userService.userIsExist(userId);

        if (text.isBlank())
            return List.of();

        return itemRepository.searchItems(userId, text.trim().toLowerCase())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    //    TODO: служебный метод
    @Override
    public List<ItemDto> getAll() {
        log.info("ItemServiceImpl - service.getAll()");
        return itemRepository.getAll().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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

    private void emptyFieldValidation(final Item item) {
        log.info("ItemServiceImpl - service.emptyFieldValidation({})", item);

        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();

        if (name == null || description == null || available == null
                || name.isBlank() || description.isBlank()) {
            String message = "Отсутствует часть обязательны полей name/description/available - " + item;
            log.warn(message);
            throw new ItemFieldValidationException(message);
        }
    }
}
