package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item createItem(@Valid @RequestBody final Item item,
                           @RequestHeader("X-Sharer-User-Id") final long userId) {
        log.info("/items - POST: createItem({}, {})", item, userId);
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@Valid @RequestBody final ItemDto item,
                           @RequestHeader("X-Sharer-User-Id") final long userId,
                           @PathVariable final long itemId) {
        log.info("/items/{} - PATCH: updateItem({}, {}, {})", itemId, item, userId, itemId);
        return itemService.updateItem(item, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@RequestHeader("X-Sharer-User-Id") final long userId,
                        @PathVariable final long itemId) {
        log.info("/items/{} - GET: getItem({}, {})", itemId, userId, itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") final long userId) {
        log.info("/items - GET: getItemsByUser({})", userId);
        return itemService.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") final long userId,
                                           @RequestParam final String text) {
        log.info("/items/search?text={} - GET: searchItems({}, {})", text, userId, text);
        return itemService.searchItems(userId, text);
    }

    //    TODO: служебный ENDPOINT
    @GetMapping("/all")
    public Collection<ItemDto> getAll() {
        log.info("/items/all - GET: getAll()");
        return itemService.getAll();
    }
}
