package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto getBooking(Long ownerId, Long bookingId);

    List<BookingResponseDto> getAllBookings(Long ownerId, String state, Integer page, Integer size);

    BookingResponseDto postBooking(BookingDto bookingDto, Long ownerId);

    BookingResponseDto approveBooking(Long ownerId, Long bookingId, Boolean approved);

    List<BookingResponseDto> getAllOwnerBookings(Long ownerId, String state, Integer page, Integer size);
}