package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.util.Map;

import static ru.practicum.shareit.common.ClientPath.BASE_SLASH_PATH;
import static ru.practicum.shareit.common.ClientPath.BASE_SPACE_PATH;

@Slf4j
@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );

        log.debug("UserClient(URL={}{})", serverUrl, API_PREFIX);
    }

    public ResponseEntity<Object> createUser(UserRequestDto userRequestDto) {
        log.debug("UserClient - baseClient.createUser({})", userRequestDto);
        return post(BASE_SPACE_PATH, userRequestDto);
    }

    public ResponseEntity<Object> updateUser(long userId, UserRequestDto userRequestDto) {
        log.debug("UserClient - baseClient.updateUser({}, {})", userId, userRequestDto);
        return patch(BASE_SLASH_PATH + userId, userRequestDto);
    }

    public ResponseEntity<Object> deleteUserById(long userId) {
        log.debug("UserClient - baseClient.deleteUserById({})", userId);
        return delete(BASE_SLASH_PATH + userId);
    }

    public ResponseEntity<Object> getUserById(long userId) {
        log.debug("UserClient - baseClient.getUserById({})", userId);
        return get(BASE_SLASH_PATH + userId);
    }

    public ResponseEntity<Object> getUsers(int from, int size) {
        log.debug("UserClient - baseClient.getUsers()");
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", null, parameters);
    }
}
