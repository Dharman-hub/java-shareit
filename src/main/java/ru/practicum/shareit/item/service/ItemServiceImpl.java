package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        validateItem(itemDto);
        checkUserExists(userId);

        Item item = ItemMapper.toItem(itemDto, userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        checkUserExists(userId);

        Item oldItem = getItemOrThrow(itemId);

        if (!oldItem.getOwner().equals(userId)) {
            throw new ForbiddenException("Редактировать вещь может только владелец");
        }

        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new ValidationException("Название вещи не может быть пустым");
            }
            oldItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new ValidationException("Описание вещи не может быть пустым");
            }
            oldItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        checkUserExists(userId);

        Item item = getItemOrThrow(itemId);

        ItemDto itemDto;

        if (item.getOwner().equals(userId)) {
            itemDto = toItemDtoWithBookings(item);
        } else {
            itemDto = ItemMapper.toItemDto(item);
        }

        itemDto.setComments(getComments(itemId));

        return itemDto;
    }

    @Override
    public List<ItemDto> getByOwnerId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        List<Item> items = itemRepository.findByOwner(userId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        List<Booking> bookings = bookingRepository
                .findByItem_IdInAndStatusOrderByStartAsc(itemIds, BookingStatus.APPROVED);

        Map<Long, List<Booking>> bookingsByItemId = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        List<Comment> comments = commentRepository.findByItemIdInOrderByCreatedDesc(itemIds);

        Map<Long, List<CommentDto>> commentsByItemId = comments.stream()
                .collect(Collectors.groupingBy(
                        Comment::getItemId,
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())
                ));

        return items.stream()
                .map(item -> toItemDtoWithBookingsAndComments(
                        item,
                        bookingsByItemId.getOrDefault(item.getId(), Collections.emptyList()),
                        commentsByItemId.getOrDefault(item.getId(), Collections.emptyList())
                ))
                .toList();
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        checkUserExists(userId);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchAvailableItems(text)
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

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new NotFoundException("Вещь с id=" + itemId + " не найдена"));
    }

    private ItemDto toItemDtoWithBookings(Item item) {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        LocalDateTime now = LocalDateTime.now();

        bookingRepository.findFirstByItem_IdAndEndBeforeAndStatusOrderByEndDesc(
                        item.getId(),
                        now,
                        BookingStatus.APPROVED
                )
                .ifPresent(booking -> itemDto.setLastBooking(toBookingShortDto(booking)));

        bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(
                        item.getId(),
                        now,
                        BookingStatus.APPROVED
                )
                .ifPresent(booking -> itemDto.setNextBooking(toBookingShortDto(booking)));

        itemDto.setComments(getComments(item.getId()));

        return itemDto;
    }

    private BookingShortDto toBookingShortDto(Booking booking) {
        BookingShortDto dto = new BookingShortDto();

        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());

        return dto;
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        getItemOrThrow(itemId);

        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Комментарий не может быть пустым");
        }

        boolean hasCompletedBooking = bookingRepository.existsByItem_IdAndBooker_IdAndStatusAndEndBefore(
                itemId,
                userId,
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );

        if (!hasCompletedBooking) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду");
        }

        Comment comment = CommentMapper.toComment(commentDto, itemId, userId);
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(savedComment, author);
    }

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(comment -> {
                    User author = userRepository.findById(comment.getAuthorId())
                            .orElseThrow(() -> new NotFoundException(
                                    "Пользователь с id=" + comment.getAuthorId() + " не найден"
                            ));

                    return CommentMapper.toCommentDto(comment, author);
                })
                .toList();
    }

    private ItemDto toItemDtoWithBookingsAndComments(Item item,
                                                     List<Booking> bookings,
                                                     List<CommentDto> comments) {
        ItemDto dto = ItemMapper.toItemDto(item);

        LocalDateTime now = LocalDateTime.now();

        bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(now))
                .reduce((first, second) -> second)
                .ifPresent(booking -> dto.setLastBooking(toBookingShortDto(booking)));

        bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .findFirst()
                .ifPresent(booking -> dto.setNextBooking(toBookingShortDto(booking)));

        dto.setComments(comments);

        return dto;
    }
}