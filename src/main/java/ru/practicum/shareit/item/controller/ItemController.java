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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto createItem(@Valid @RequestBody final ItemRequestDto item,
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
                                                 @RequestParam(defaultValue = "0") final int from,
                                                 @RequestParam(defaultValue = "10") final int size) {
        log.debug("/items - GET: getItemsByUser({})", ownerId);
        return itemService.getItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                             @RequestParam final String text,
                                             @RequestParam(defaultValue = "0") final int from,
                                             @RequestParam(defaultValue = "10") final int size) {
        log.debug("/items/search?text={} - GET: searchItems({}, {})", text, userId, text);
        return itemService.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@Valid @RequestBody final CommentRequestDto commentDto,
                                            @RequestHeader("X-Sharer-User-Id") final Long authorId,
                                            @PathVariable final long itemId) {
        log.debug("/items/{}/commentDto - POST: createComment({}, {}, {})", itemId, commentDto, authorId, itemId);
        return itemService.createComment(commentDto, authorId, itemId);
    }
}
