package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;
    private final BookingMapper mapper;

    @PostMapping
    public BookingDtoResponse createBookingRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        Booking booking = mapper.toBooking(bookingDto, userId);
        return mapper.toBookingDtoResponse(service.createBookingRequest(booking, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBookingStatusByOwner(
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam boolean approved) {
        return mapper.toUpdateBookingDtoResponse(service.updateBookingStatusByOwner(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingDetails(
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return mapper.toBookingDtoResponse(service.getBookingDetails(bookingId, userId));
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsByAuthor(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllBookingsByAuthor(state, userId).stream()
                .map(mapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return service.getAllBookingByOwner(state, userId).stream()
                .map(mapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }
}