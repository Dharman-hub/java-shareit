package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItem_OwnerOrderByStartDesc(Long ownerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByItem_OwnerAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByItem_OwnerAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByItem_OwnerAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Booking> findByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<Booking> findFirstByItem_IdAndEndBeforeAndStatusOrderByEndDesc(
            Long itemId,
            LocalDateTime now,
            BookingStatus status
    );

    Optional<Booking> findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(
            Long itemId,
            LocalDateTime now,
            BookingStatus status
    );

    List<Booking> findByItem_IdInAndStatusOrderByStartAsc(
            List<Long> itemIds,
            BookingStatus status
    );

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndBefore(
            Long itemId,
            Long userId,
            BookingStatus status,
            LocalDateTime end
    );
}