package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void toItem_shouldMapDtoToItem() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Дрель");
        dto.setDescription("Аккумуляторная дрель");
        dto.setAvailable(true);
        dto.setRequestId(10L);

        Item item = ItemMapper.toItem(dto, 2L);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Дрель");
        assertThat(item.getDescription()).isEqualTo("Аккумуляторная дрель");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(2L);
        assertThat(item.getRequestId()).isEqualTo(10L);
    }

    @Test
    void toItemDto_shouldMapItemToDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setOwner(2L);
        item.setRequestId(10L);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("Аккумуляторная дрель");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getOwnerId()).isEqualTo(2L);
        assertThat(dto.getRequestId()).isEqualTo(10L);
    }
}