package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMapperTest {

    @Test
    void toItemRequest_shouldMapDtoToEntity() {
        User user = new User();
        user.setId(1L);

        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Нужна дрель");

        ItemRequest request = RequestMapper.toItemRequest(dto, user);

        assertThat(request.getDescription()).isEqualTo("Нужна дрель");
        assertThat(request.getRequestor()).isEqualTo(user);
        assertThat(request.getCreated()).isNotNull();
    }

    @Test
    void toItemRequestDto_shouldMapEntityToDtoWithItems() {
        User user = new User();
        user.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(10L);
        request.setDescription("Нужна дрель");
        request.setRequestor(user);

        Item item = new Item();
        item.setId(100L);
        item.setName("Дрель");
        item.setOwner(2L);

        ItemRequestDto dto = RequestMapper.toItemRequestDto(request, List.of(item));

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().getFirst().getId()).isEqualTo(100L);
        assertThat(dto.getItems().getFirst().getName()).isEqualTo("Дрель");
        assertThat(dto.getItems().getFirst().getOwnerId()).isEqualTo(2L);
    }
}