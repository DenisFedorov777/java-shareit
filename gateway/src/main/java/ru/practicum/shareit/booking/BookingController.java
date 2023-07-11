package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, ownerId, from, size);

        return bookingClient.getAllBookings(ownerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @PathVariable Long bookingId) {
        return bookingClient.getBooking(ownerId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> postBooking(@Valid @RequestBody BookingDto bookingDto,
                                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingClient.postBooking(bookingDto, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, ownerId, from, size);

        return bookingClient.getAllOwnerBookings(ownerId, state, from, size);
    }
}