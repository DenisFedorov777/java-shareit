package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.statuses.BookingState;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDtoResponse createBookingRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        return (service.createBookingRequest(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBookingStatusByOwner(
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam boolean approved) {
        return service.updateBookingStatusByOwner(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingDetails(
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getBookingDetails(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsByAuthor(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllBookingsByAuthor(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return service.getAllBookingByOwner(state, userId);
    }
}