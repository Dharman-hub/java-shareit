package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

public class BookingMapper {

    public static Booking toBooking(NewBookingDto dto, Long bookerId) {
        Booking booking = new Booking();

        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItemId(dto.getItemId());
        booking.setBookerId(bookerId);
        booking.setStatus(BookingStatus.WAITING);

        return booking;
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
        bookerDto.setId(booking.getBookerId());

        dto.setItem(itemDto);
        dto.setBooker(bookerDto);

        return dto;
    }
}