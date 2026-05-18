package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class RequestMapper {

    public static ItemRequest toItemRequest(NewItemRequestDto dto, User requestor) {
        ItemRequest request = new ItemRequest();

        request.setDescription(dto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        return request;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items) {
        ItemRequestDto dto = new ItemRequestDto();

        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(items.stream()
                .map(RequestMapper::toAnswerDto)
                .toList());

        return dto;
    }

    private static ItemRequestAnswerDto toAnswerDto(Item item) {
        ItemRequestAnswerDto dto = new ItemRequestAnswerDto();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner());

        return dto;
    }
}