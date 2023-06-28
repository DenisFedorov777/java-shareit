package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.statuses.BookingState;

import java.util.List;

public interface BookingService {
    BookingDtoResponse createBookingRequest(BookingDto bookingDto, Long userId);

    BookingDtoResponse updateBookingStatusByOwner(Long bookingId, Long userId, boolean approved);

    BookingDtoResponse getBookingDetails(Long bookingId, Long userId);

    List<BookingDtoResponse> getAllBookingsByAuthor(BookingState state, Long userId);

    List<BookingDtoResponse> getAllBookingByOwner(BookingState state, Long userId);
}