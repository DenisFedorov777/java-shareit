package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.statuses.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private static BookingResponseDto bookingDto1;
    private static BookingResponseDto bookingDto2;
    private static final User owner = new User(1L, "TestUser", "testUser@testUser.com");
    private static final ItemDto itemDto = new ItemDto(
            1L, "ItemName", "ItemDesc", true, 1L);
    private static final LocalDateTime nowTime = LocalDateTime.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    @BeforeAll
    static void setUp() {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        User booker = new User();
        booker.setId(2L);
        Booking booking1 = new Booking(1L, booker, nowTime.plusDays(1),
                nowTime.plusDays(2), item, Status.WAITING);
        Booking booking2 = new Booking(2L, booker, nowTime.plusDays(3),
                nowTime.plusDays(4), item, Status.APPROVED);
        bookingDto1 = BookingMapper.toBookingResponseDto(booking1);
        bookingDto2 = BookingMapper.toBookingResponseDto(booking2);
    }

    @Test
    public void getAllBookingsTest() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingDto1, bookingDto2);

        when(bookingService.getAllBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(bookings.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(bookingDto1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(bookingDto2.getId()));
    }

    @Test
    public void getBookingTest() throws Exception {

        when(bookingService.getBooking(
                anyLong(), anyLong())).thenReturn(bookingDto1);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookingDto1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start")
                        .value(nowTime.plusDays(1).format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end")
                        .value(nowTime.plusDays(2).format(formatter)));
    }

    @Test
    public void postBookingTest() throws Exception {
        when(bookingService.postBooking(any(BookingDto.class), anyLong()))
                .thenReturn(bookingDto1);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":2,\"start\":\"" + nowTime.plusHours(1) +
                                "\",\"end\":\"" + nowTime.plusHours(2) + "\"}")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookingDto1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start")
                        .value(nowTime.plusDays(1).format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end")
                        .value(nowTime.plusDays(2).format(formatter)));
    }

    @Test
    public void approveBookingTest() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto1);

        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 12345L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").value(nowTime.plusDays(1).format(formatter)))
                .andExpect(jsonPath("$.end").value(nowTime.plusDays(2).format(formatter)))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("ItemName"))
                .andExpect(jsonPath("$.booker.id").value(2));
    }

    @Test
    public void getAllOwnerBookingsTest() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingDto1, bookingDto2);

        when(bookingService.getAllOwnerBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(bookings.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(bookingDto1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(bookingDto2.getId()));
    }
}