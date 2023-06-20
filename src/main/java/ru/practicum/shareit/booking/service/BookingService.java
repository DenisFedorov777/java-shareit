package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.statuses.BookingState;

import java.util.List;

public interface BookingService {
    Booking createBookingRequest(Booking booking, Long userId);

    Booking updateBookingStatusByOwner(Long bookingId, Long userId, boolean approved);

    Booking getBookingDetails(Long bookingId, Long userId);

    List<Booking> getAllBookingsByAuthor(BookingState state, Long userId);

    List<Booking> getAllBookingByOwner(BookingState state, Long userId);
}