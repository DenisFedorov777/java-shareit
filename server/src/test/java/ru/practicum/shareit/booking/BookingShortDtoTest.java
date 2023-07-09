package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.dto.BookingShortDto;
import ru.practicum.shareit.booking.statuses.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookingShortDtoTest {
    @Test
    public void testBookingShortDtoCreation() {
        Long bookingId = 1L;
        User booker = new User();
        Item item = new Item();
        Status status = Status.WAITING;

        BookingShortDto bookingShortDto = new BookingShortDto(bookingId, item, booker, status);

        assertNotNull(bookingShortDto);
        assertEquals(bookingId, bookingShortDto.getId());
        assertEquals(booker, bookingShortDto.getBooker());
        assertEquals(item, bookingShortDto.getItem());
        assertEquals(status, bookingShortDto.getStatus());
    }
}
