package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private LocalDateTime nowTime = LocalDateTime.now();
    private User user = new User();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L, "TestDescription", user, nowTime, new ArrayList<>());
    private final ItemRequestDto itemRequestDto2 = new ItemRequestDto(
            2L, "TestDescription2", user, nowTime, new ArrayList<>());

    @Test
    public void postItemRequestTest() throws Exception {
        when(itemRequestService.postItemRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\"name\":\"Дрель\",\"description\":\"Простая дрель\",\"available\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("TestDescription"))
                .andExpect(jsonPath("$.created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$.items").value(new ArrayList<>()));
    }

    @Test
    public void getItemRequestByOwnerTest() throws Exception {
        List<ItemRequestDto> itemRequestList = List.of(itemRequestDto, itemRequestDto2);
        when(itemRequestService.getItemRequestsByRequestor(anyLong()))
                .thenReturn(itemRequestList);

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("TestDescription"))
                .andExpect(jsonPath("$[0].created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$[0].items").value(new ArrayList<>()))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("TestDescription2"))
                .andExpect(jsonPath("$[1].created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$[1].items").value(new ArrayList<>()));
    }

    @Test
    public void getAllItemRequestByPageTest() throws Exception {
        List<ItemRequestDto> itemRequestList = List.of(itemRequestDto, itemRequestDto2);
        when(itemRequestService.getItemRequests(anyLong(), anyLong(), anyLong()))
                .thenReturn(itemRequestList);

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("TestDescription"))
                .andExpect(jsonPath("$[0].created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$[0].items").value(new ArrayList<>()))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("TestDescription2"))
                .andExpect(jsonPath("$[1].created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$[1].items").value(new ArrayList<>()));
    }

    @Test
    public void getItemRequestTest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("TestDescription"))
                .andExpect(jsonPath("$.created").value(nowTime.format(formatter)))
                .andExpect(jsonPath("$.items").value(new ArrayList<>()));
    }
}