package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody final ItemRequestDto item,
                                             @RequestHeader("X-Sharer-User-Id") final Long ownerId) {
        log.debug("/items - POST: createItem({}, {})", item, ownerId);
        return itemClient.createItem(item, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody final ItemRequestDto item,
                                             @RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                             @PathVariable final long itemId) {
        log.debug("/items/{} - PATCH: updateItem({}, {}, {})", itemId, item, ownerId, itemId);
        return itemClient.updateItem(item, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                          @PathVariable final long itemId) {
        log.debug("/items/{} - GET: getItem({}, {})", itemId, ownerId, itemId);
        return itemClient.getItem(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                                  @RequestParam(defaultValue = "10") @Positive final int size) {
        log.debug("/items?from={}&size={} - GET: getItemsByUser({}, {}, {})", from, size, ownerId, from, size);
        return itemClient.getItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                              @RequestParam final String text,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                              @RequestParam(defaultValue = "10") @Positive final int size) {
        log.debug("/items/search?text={}&from={}&size={} - GET: searchItems({}, {}, {}, {})", text, from, size, ownerId, text, from, size);
        return itemClient.serachItems(ownerId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody final CommentRequestDto commentDto,
                                                @RequestHeader("X-Sharer-User-Id") final Long authorId,
                                                @PathVariable final long itemId) {
        log.debug("/items/{}/commentDto - POST: createComment({}, {}, {})", itemId, commentDto, authorId, itemId);
        return itemClient.createComment(commentDto, authorId, itemId);
    }
}
