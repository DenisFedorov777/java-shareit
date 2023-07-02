package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.booker.id = ?2 " +
            "AND b.end < current_timestamp")
    List<Booking> findByItemIdAndOwnerId(Long itemId, Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllBookings(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.start <= current_timestamp AND b.end >= current_timestamp " +
            "ORDER BY b.start DESC")
    Page<Booking> findCurrentBookings(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.end <= current_timestamp " +
            "ORDER BY b.start DESC")
    Page<Booking> findPastBookings(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.start >= current_timestamp " +
            "ORDER BY b.start DESC")
    Page<Booking> findFutureBookings(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.start DESC")
    Page<Booking> findBookingsByWaiting(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    Page<Booking> findBookingsByRejected(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "ORDER BY b.start DESC")
    Page<Booking> findOwnerAllBookings(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start <= current_timestamp AND b.end >= current_timestamp " +
            "ORDER BY b.start DESC")
    Page<Booking> findOwnerCurrentBookings(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.end <= current_timestamp " +
            "ORDER BY b.start DESC")
    Page<Booking> findOwnerPastBookings(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start >= current_timestamp " +
            "ORDER BY b.start DESC")
    Page<Booking> findOwnerFutureBookings(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.start DESC")
    Page<Booking> findOwnerBookingsByWaiting(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    Page<Booking> findOwnerBookingsByRejected(Long ownerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.id = ?1 " +
            "AND i.owner.id = ?2 " +
            "AND b.start < current_timestamp " +
            "AND b.status NOT LIKE 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> findLastBookingForItem(Long itemId, Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.id = ?1 " +
            "AND i.owner.id = ?2 " +
            "AND b.start > current_timestamp " +
            "AND b.status NOT LIKE 'REJECTED' " +
            "ORDER BY b.start ASC")
    List<Booking> findNextBookingForItem(Long itemId, Long ownerId);
}