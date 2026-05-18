package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Long itemId, Item item);

    Item getById(Long itemId);

    List<Item> getByOwnerId(Long ownerId);

    List<Item> search(String text);
}
