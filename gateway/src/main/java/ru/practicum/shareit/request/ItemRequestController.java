package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final RequestClient requestClient;

    public ItemRequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public Object create(@RequestHeader(USER_ID_HEADER) Long userId,
                         @RequestBody NewItemRequestDto requestDto) {

        if (requestDto.getDescription() == null ||
                requestDto.getDescription().isBlank()) {

            throw new ValidationException(
                    "Описание запроса не может быть пустым"
            );
        }

        return requestClient.create(userId, requestDto);
    }

    @GetMapping
    public Object getUserRequests(
            @RequestHeader(USER_ID_HEADER) Long userId) {

        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public Object getAllRequests(
            @RequestHeader(USER_ID_HEADER) Long userId) {

        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public Object getById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long requestId) {

        return requestClient.getById(userId, requestId);
    }
}