package ru.practicum.shareit.item.service;


import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        validateItem(itemDto);
        userService.getById(userId);

        Item item = ItemMapper.toItem(itemDto, userId);
        return ItemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item oldItem = itemStorage.getById(itemId);

        if (!oldItem.getOwner().equals(userId)) {
            throw new ForbiddenException("Редактировать вещь может только владелец");
        }

        Item item = ItemMapper.toItem(itemDto, userId);
        return ItemMapper.toItemDto(itemStorage.update(itemId, item));
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        userService.getById(userId);
        return ItemMapper.toItemDto(itemStorage.getById(itemId));
    }

    @Override
    public List<ItemDto> getByOwnerId(Long userId) {
        userService.getById(userId);

        return itemStorage.getByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        userService.getById(userId);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemStorage.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
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
}