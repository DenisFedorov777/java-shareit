package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void getItemsTest() {
        UserDto userDto = new UserDto(1L, "TestUser", "testUser@test.com");
        userService.postUser(userDto);
        ItemDto itemDto = new ItemDto(1L, "ItemName", "ItemDesc", true, null);
        itemService.postItem(itemDto, 1L);
        List<ItemDtoWithBooking> items = itemService.getItems(1L, 0, 10);
        assertEquals(1, items.size());
        assertEquals(itemDto.getId(), items.get(0).getId());
        assertEquals(itemDto.getName(), items.get(0).getName());
        assertEquals(itemDto.getAvailable(), items.get(0).getAvailable());
        assertEquals(itemDto.getDescription(), items.get(0).getDescription());
    }
}