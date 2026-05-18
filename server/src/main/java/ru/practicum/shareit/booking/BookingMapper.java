package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return toBookingDto(booking, booking.getItem());
    }

    public static BookingDto toBookingDto(Booking booking,
                                          ru.practicum.shareit.item.model.Item item) {
        BookingDto dto = new BookingDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus().name());

        BookingDto.Item itemDto = new BookingDto.Item();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());

        BookingDto.Booker bookerDto = new BookingDto.Booker();
        bookerDto.setId(booking.getBooker().getId());

        dto.setItem(itemDto);
        dto.setBooker(bookerDto);

        return dto;
    }
}