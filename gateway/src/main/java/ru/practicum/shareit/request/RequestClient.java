package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String serverUrl) {
        super(new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .build());
    }

    public Object create(Long userId, NewItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public Object getUserRequests(Long userId) {
        return get("", userId);
    }

    public Object getAllRequests(Long userId) {
        return get("/all", userId);
    }

    public Object getById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}