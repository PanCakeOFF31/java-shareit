package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

import static ru.practicum.shareit.common.ClientPath.BASE_SLASH_PATH;
import static ru.practicum.shareit.common.ClientPath.BASE_SPACE_PATH;

@Slf4j
@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemRequestDto itemRequestDto, long ownerId) {
        log.debug("ItemClient - baseClient.createItem({}, {})", itemRequestDto, ownerId);
        return post(BASE_SPACE_PATH, ownerId, itemRequestDto);
    }

    public ResponseEntity<Object> updateItem(ItemRequestDto itemRequestDto, long ownerId, long itemId) {
        log.debug("ItemClient - baseClient.updateItem({}, {}, {})", itemRequestDto, ownerId, itemId);
        return patch(BASE_SLASH_PATH + itemId, ownerId, itemRequestDto);
    }

    public ResponseEntity<Object> getItem(long itemId, long ownerId) {
        log.debug("ItemClient - baseClient.getItem({}, {})", ownerId, itemId);
        return get(BASE_SLASH_PATH + itemId, ownerId);
    }

    public ResponseEntity<Object> getItemsByOwner(long ownerId, int from, int size) {
        log.debug("ItemClient - baseClient.getItemsByOwner({}, {}, {})", ownerId, from, size);
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get(BASE_SPACE_PATH + "?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> serachItems(long ownerId, String text, int from, int size) {
        log.debug("ItemClient - baseClient.serachItems({}, {}, {}, {})", ownerId, text, from, size);
        Map<String, Object> parameters = Map.of("text", text, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> createComment(CommentRequestDto commentRequestDto, long authorId, long itemId) {
        log.debug("ItemClient - baseClient.createComment({}, {}, {})", commentRequestDto, authorId, itemId);
        return post(BASE_SLASH_PATH + itemId + "/comment", authorId, commentRequestDto);
    }
}
