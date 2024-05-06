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
import java.util.Collection;
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
    public ItemResponseDto getItem(@PathVariable final long itemId,
                                   @RequestHeader("X-Sharer-User-Id") final Long ownerId) {
        log.debug("/items/{} - GET: getItem({}, {})", itemId, itemId, ownerId);
        return itemService.getItemDtoById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemResponseDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") final Long ownerId) {
        log.debug("/items - GET: getItemsByUser({})", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                             @RequestParam final String text) {
        log.debug("/items/search?text={} - GET: searchItems({}, {})", text, userId, text);
        return itemService.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
//    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@Valid @RequestBody final CommentRequestDto commentDto,
                                            @RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                            @PathVariable final long itemId) {
        log.debug("/items/{}/commentDto - POST: createComment({}, {}, {})", itemId, commentDto, ownerId, itemId);
        return itemService.createComment(commentDto, ownerId, itemId);
    }


    //    TODO: служебный ENDPOINT
    @GetMapping("/all/item")
    public Collection<ItemResponseDto> getAllItems() {
        log.debug("/items/all - GET: getAllItems()");
        return itemService.getAllItems();
    }

    //    TODO: служебный ENDPOINT
    @GetMapping("/all/comment")
    public Collection<CommentResponseDto> getAllComments() {
        log.debug("/items/all - GET: getAllComments()");
        return itemService.getAllComments();
    }
}
