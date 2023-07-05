package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingResponseDto> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getAllBookings(ownerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @PathVariable Long bookingId) {
        return bookingService.getBooking(ownerId, bookingId);
    }

    @PostMapping
    public BookingResponseDto postBooking(@Valid @RequestBody BookingDto bookingDto,
                                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.postBooking(bookingDto, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getAllOwnerBookings(ownerId, state, from, size);
    }
}