package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto create(Long userId, NewBookingDto bookingDto) {
        getUserOrThrow(userId);

        Item item = getItemOrThrow(bookingDto.getItemId());

        if (item.getOwner().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        validateDates(bookingDto.getStart(), bookingDto.getEnd());

        Booking booking = BookingMapper.toBooking(bookingDto, userId);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(savedBooking, item);
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);
        Item item = getItemOrThrow(booking.getItemId());

        if (!item.getOwner().equals(userId)) {
            throw new ForbiddenException("Подтвердить бронирование может только владелец вещи");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус уже изменён");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(savedBooking, item);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        checkUserExists(userId);

        Booking booking = getBookingOrThrow(bookingId);
        Item item = getItemOrThrow(booking.getItemId());

        if (!booking.getBookerId().equals(userId)
                && !item.getOwner().equals(userId)) {
            throw new ForbiddenException("Просматривать бронирование может только владелец или автор");
        }

        return BookingMapper.toBookingDto(booking, item);
    }

    @Override
    public List<BookingDto> getByBooker(Long userId, BookingState state) {
        checkUserExists(userId);

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository
                    .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository
                    .findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository
                    .findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(booking -> BookingMapper.toBookingDto(
                        booking,
                        getItemOrThrow(booking.getItemId())
                ))
                .toList();
    }

    @Override
    public List<BookingDto> getByOwner(Long userId, BookingState state) {
        checkUserExists(userId);

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByOwnerId(userId);
            case CURRENT -> bookingRepository.findCurrentByOwnerId(userId, now);
            case PAST -> bookingRepository.findPastByOwnerId(userId, now);
            case FUTURE -> bookingRepository.findFutureByOwnerId(userId, now);
            case WAITING -> bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(booking -> BookingMapper.toBookingDto(
                        booking,
                        getItemOrThrow(booking.getItemId())
                ))
                .toList();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }

    private void checkUserExists(Long userId) {
        getUserOrThrow(userId);
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ValidationException("Даты не указаны");
        }

        if (!start.isBefore(end)) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }
    }
}