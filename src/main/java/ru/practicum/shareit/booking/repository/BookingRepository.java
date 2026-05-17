package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("""
            SELECT b
            FROM Booking b
            JOIN Item i ON b.itemId = i.id
            WHERE i.owner = :ownerId
            ORDER BY b.start DESC
            """)
    List<Booking> findByOwnerId(Long ownerId);

    @Query("""
            SELECT b
            FROM Booking b
            JOIN Item i ON b.itemId = i.id
            WHERE i.owner = :ownerId
            AND b.start < :now
            AND b.end > :now
            ORDER BY b.start DESC
            """)
    List<Booking> findCurrentByOwnerId(Long ownerId, LocalDateTime now);

    @Query("""
            SELECT b
            FROM Booking b
            JOIN Item i ON b.itemId = i.id
            WHERE i.owner = :ownerId
            AND b.end < :now
            ORDER BY b.start DESC
            """)
    List<Booking> findPastByOwnerId(Long ownerId, LocalDateTime now);

    @Query("""
            SELECT b
            FROM Booking b
            JOIN Item i ON b.itemId = i.id
            WHERE i.owner = :ownerId
            AND b.start > :now
            ORDER BY b.start DESC
            """)
    List<Booking> findFutureByOwnerId(Long ownerId, LocalDateTime now);

    @Query("""
            SELECT b
            FROM Booking b
            JOIN Item i ON b.itemId = i.id
            WHERE i.owner = :ownerId
            AND b.status = :status
            ORDER BY b.start DESC
            """)
    List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
            Long itemId,
            LocalDateTime now,
            BookingStatus status
    );

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
            Long itemId,
            LocalDateTime now,
            BookingStatus status
    );

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
            Long itemId,
            Long bookerId,
            BookingStatus status,
            LocalDateTime now
    );
}