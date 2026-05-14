package ru.practicum.shareit.item.storage;


import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item create(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long itemId, Item item) {
        Item oldItem = getById(itemId);

        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }

        return oldItem;
    }

    @Override
    public Item getById(Long itemId) {
        Item item = items.get(itemId);

        if (item == null) {
            throw new NotFoundException("Вещь с id " + itemId + " не найдена");
        }

        return item;
    }

    @Override
    public List<Item> getByOwnerId(Long ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        String searchText = text.toLowerCase();

        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(searchText)
                                || item.getDescription().toLowerCase().contains(searchText)
                )
                .toList();
    }
}