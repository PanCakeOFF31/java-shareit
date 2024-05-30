package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ReqRequestDto;

import java.util.Map;

import static ru.practicum.shareit.common.ClientPath.BASE_SLASH_PATH;
import static ru.practicum.shareit.common.ClientPath.BASE_SPACE_PATH;

@Slf4j
@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(ReqRequestDto reqRequestDto, long requesterId) {
        log.debug("RequestClient - baseClient.createRequest({}, {})", reqRequestDto, requesterId);
        return post(BASE_SPACE_PATH, requesterId, reqRequestDto);
    }

    public ResponseEntity<Object> getRequestsForRequester(long requesterId, int from, int size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        log.debug("RequestClient - baseClient.getRequestsForRequester({}, {},  {})", requesterId, from, size);
        return get(BASE_SPACE_PATH + "?from={from}&size={size}", requesterId, parameters);
    }

    public ResponseEntity<Object> getAllRequests(long requesterId, int from, int size) {
        log.debug("RequestClient - baseClient.getAllRequests({}, {},  {})", requesterId, from, size);
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("/all?from={from}&size={size}", requesterId, parameters);
    }

    public ResponseEntity<Object> getRequestByRequestId(long requestId, long requesterId) {
        log.debug("RequestClient - baseClient.getRequestByRequestId({}, {})", requesterId, requestId);
        return get(BASE_SLASH_PATH + requestId, requesterId);
    }
}
