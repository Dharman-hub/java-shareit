package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl) {
        super(new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .build());
    }

    public Object getAll() {
        return get("");
    }

    public Object getById(Long userId) {
        return get("/" + userId);
    }

    public Object create(UserDto userDto) {
        return post("", userDto);
    }

    public Object update(Long userId, UserDto userDto) {
        return patch("/" + userId, userDto);
    }

    public void delete(Long userId) {
        delete("/" + userId);
    }
}