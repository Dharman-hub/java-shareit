package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void toBookingDto_shouldMapEntityToDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");

        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setId(10L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto dto = BookingMapper.toBookingDto(booking, item);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getItem().getId()).isEqualTo(1L);
        assertThat(dto.getBooker().getId()).isEqualTo(2L);
        assertThat(dto.getStatus()).isEqualTo("WAITING");
    }
}