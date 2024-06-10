package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto createItem(@RequestBody final ItemRequestDto item,
                                      @RequestHeader("X-Sharer-User-Id") final Long ownerId) {
        log.debug("/items - POST: createItem({}, {})", item, ownerId);
        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestBody final ItemRequestDto item,
                                      @RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                      @PathVariable final long itemId) {
        log.debug("/items/{} - PATCH: updateItem({}, {}, {})", itemId, item, ownerId, itemId);
        return itemService.updateItem(item, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                   @PathVariable final long itemId) {
        log.debug("/items/{} - GET: getItem({}, {})", itemId, itemId, ownerId);
        return itemService.getItemDtoById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemResponseDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                                 @RequestParam final int from,
                                                 @RequestParam final int size) {
        log.debug("/items?from={}&size={} - GET: getItemsByUser({}, {}, {})", from, size, ownerId, from, size);
        return itemService.getItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                             @RequestParam final String text,
                                             @RequestParam final int from,
                                             @RequestParam final int size) {
        log.debug("/items/search?text={}&from={}&size={} - GET: searchItems({}, {}, {}, {})", text, from, size, ownerId, text, from, size);
        return itemService.searchItems(ownerId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestBody final CommentRequestDto commentDto,
                                            @RequestHeader("X-Sharer-User-Id") final Long authorId,
                                            @PathVariable final long itemId) {
        log.debug("/items/{}/commentDto - POST: createComment({}, {}, {})", itemId, commentDto, authorId, itemId);
        return itemService.createComment(commentDto, authorId, itemId);
    }
}
