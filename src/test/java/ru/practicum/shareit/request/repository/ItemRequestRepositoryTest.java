package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    User user;
    Item item;

    void createUserAndItem() {
        user = new User();
        user.setId(1L);
        user.setName("TestUser");
        user.setEmail("testuser@user.com");
        userRepository.save(user);

        item = new Item();
        item.setName("Item1");
        item.setOwner(user);
        item.setDescription("Description1");

        itemRepository.save(item);
    }

    @Test
    void findByRequestorId() {
        createUserAndItem();
        Long requestorId = 1L;

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(user);
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("Описание");
        itemRequestRepository.save(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequestor(user);
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("Описание2");
        itemRequestRepository.save(itemRequest2);

        List<ItemRequest> result = itemRequestRepository.findByRequestorId(requestorId);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void findItemRequestsByExcludingRequestorId() {
        createUserAndItem();
        Long requestorId = 1L;

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(user);
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("Описание");
        itemRequestRepository.save(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequestor(user);
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("Второе описание");
        itemRequestRepository.save(itemRequest2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemRequest> resultPage = itemRequestRepository.findItemRequestsByExcludingRequestorId(requestorId, pageable);

        assertThat(resultPage).isEmpty();
        assertThat(resultPage.getTotalElements()).isZero();
    }

    @Test
    void findItemRequestsByExcludingRequestorIdWithoutPageable() {
        createUserAndItem();
        Long requestorId = 1L;

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(user);
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("Описание");
        itemRequestRepository.save(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequestor(user);
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("Описание2");
        itemRequestRepository.save(itemRequest2);
        List<ItemRequest> result = itemRequestRepository.findItemRequestsByExcludingRequestorId(requestorId);
        assertThat(result).isEmpty();
        assertThat(result.size()).isZero();
    }
}