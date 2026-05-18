package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public Object create(@RequestHeader(USER_ID_HEADER) Long userId,
                         @RequestBody ItemDto itemDto) {
        validateItem(itemDto);

        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Object update(@RequestHeader(USER_ID_HEADER) Long userId,
                         @PathVariable Long itemId,
                         @RequestBody ItemDto itemDto) {
        validateItemForUpdate(itemDto);

        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Object getById(@RequestHeader(USER_ID_HEADER) Long userId,
                          @PathVariable Long itemId) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public Object getByOwnerId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getByOwnerId(userId);
    }

    @GetMapping("/search")
    public Object search(@RequestHeader("X-Sharer-User-Id") long userId,
                         @RequestParam String text) {
        log.info("Search items, userId={}, text={}", userId, text);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemClient.search(userId, text);
    }

    private void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус доступности вещи должен быть указан");
        }
    }

    private void validateItemForUpdate(ItemDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым");
        }

        if (itemDto.getDescription() != null && itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }
    }

    @PostMapping("/{itemId}/comment")
    public Object addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                             @PathVariable Long itemId,
                             @RequestBody NewCommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Комментарий не может быть пустым");
        }

        return itemClient.addComment(userId, itemId, commentDto);
    }
}