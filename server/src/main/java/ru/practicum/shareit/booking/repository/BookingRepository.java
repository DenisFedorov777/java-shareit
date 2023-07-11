package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.statuses.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long ownerId, LocalDateTime time);

    Page<Booking> findByBooker_IdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long ownerId, Status status, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, Status status, Pageable pageable);

    List<Booking> findAllByItem_IdAndItem_Owner_IdAndStartBeforeAndStatusNotOrderByStartDesc(
            Long itemId, Long ownerId, LocalDateTime time, Status status);

    List<Booking> findAllByItem_IdAndItem_Owner_IdAndStartAfterAndStatusNotOrderByStartAsc(
            Long itemId, Long ownerId, LocalDateTime time, Status status);
}