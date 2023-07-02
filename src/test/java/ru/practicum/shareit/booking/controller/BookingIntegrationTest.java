package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.statuses.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void getAllBookingsTest() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(3);
        UserDto userDto = new UserDto(null, "TestUser", "testUser@test.com");
        UserDto userDto2 = new UserDto(null, "TestUser2", "testUser2@test.com");
        ItemDto itemDto = new ItemDto(null, "ItemName", "ItemDesc", true, null);
        BookingDto bookingDto = new BookingDto(
                null, start, end, 1L);
        userService.postUser(userDto);
        userService.postUser(userDto2);
        itemService.postItem(itemDto, 1L);
        bookingService.postBooking(bookingDto, 2L);

        List<BookingResponseDto> resultList = bookingService.getAllBookings(2L, "ALL", 0, 10);

        assertEquals(1, resultList.size());
        assertEquals(1L, resultList.get(0).getId());
        assertEquals(itemDto.getName(), resultList.get(0).getItem().getName());
        assertEquals(userDto2.getName(), resultList.get(0).getBooker().getName());
        assertEquals(Status.WAITING, resultList.get(0).getStatus());
    }
}