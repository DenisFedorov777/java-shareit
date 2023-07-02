package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.statuses.Status;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    User user1;
    Item item1;
    Item item2;
    Comment comment1;
    Comment comment2;
    Booking booking1;
    Booking booking2;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;

    @BeforeEach
    public void setUp() {
        user1 = new User(1L, "TestUser", "testuser@email.com");
        item1 = new Item(1L, "Item1", "Item1-Desc", true, user1, null);
        item2 = new Item(2L, "Item2", "Item2-Desc", true, user1, null);
        booking1 = new Booking(1L, user1, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item1, Status.WAITING);
        booking2 = new Booking(2L, user1, LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(10), item1, Status.WAITING);
        comment1 = new Comment(1L, item1, user1, LocalDateTime.now(), "Comentar");
        comment2 = new Comment(2L, item1, user1, LocalDateTime.now(), "Comentar2");
        itemRequest1 = new ItemRequest(
                1L, "RequestDesc", user1, LocalDateTime.now(), new ArrayList<>());
        itemRequest2 = new ItemRequest(
                2L, "RequestDesc2", user1, LocalDateTime.now().plusHours(3), new ArrayList<>());
    }

    @Test
    public void getItemRequestsShouldThrowUserNotFoundException() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequests(0L, 10L, user1.getId()));
    }

    @Test
    public void getItemRequestsShouldReturnWithFromAndSize() {
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        List<ItemRequest> mockedRequests = List.of(itemRequest1, itemRequest2);
        Page<ItemRequest> mockedRequestsPage = new PageImpl<>(mockedRequests, pageable, mockedRequests.size());

        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findItemRequestsByExcludingRequestorId(user1.getId(), pageable))
                .thenReturn(mockedRequestsPage);

        List<ItemRequestDto> resultList = itemRequestService.getItemRequests(0L, 10L, user1.getId());

        assertEquals(mockedRequests.size(), resultList.size());
        assertEquals(mockedRequests.get(0).getId(), resultList.get(0).getId());
        assertEquals(mockedRequests.get(1).getId(), resultList.get(1).getId());
    }

    @Test
    public void getItemRequestsShouldReturnListWithNullFromAndSize() {
        List<ItemRequest> mockedRequests = List.of(itemRequest1, itemRequest2);

        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository
                .findItemRequestsByExcludingRequestorId(user1.getId())).thenReturn(mockedRequests);

        List<ItemRequestDto> resultList = itemRequestService.getItemRequests(null, null, user1.getId());

        assertEquals(mockedRequests.size(), resultList.size());
        assertEquals(mockedRequests.get(0).getId(), resultList.get(0).getId());
        assertEquals(mockedRequests.get(1).getId(), resultList.get(1).getId());
    }

    @Test
    public void getItemRequestsByRequestorTest() {
        List<ItemRequest> mockedRequests = List.of(itemRequest1, itemRequest2);

        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findByRequestorId(anyLong())).thenReturn(mockedRequests);

        List<ItemRequestDto> resultList = itemRequestService.getItemRequestsByRequestor(user1.getId());

        assertEquals(mockedRequests.size(), resultList.size());
        assertEquals(mockedRequests.get(0).getId(), resultList.get(0).getId());
        assertEquals(mockedRequests.get(1).getId(), resultList.get(1).getId());
    }

    @Test
    public void postItemRequestShouldCreateTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest1);

        ItemRequestDto resultRequest = itemRequestService.postItemRequest(
                ItemRequestMapper.toItemRequestDto(itemRequest1), user1.getId());

        assertEquals(itemRequest1.getId(), resultRequest.getId());
        assertEquals(itemRequest1.getRequestor(), resultRequest.getRequestor());
        assertEquals(itemRequest1.getDescription(), resultRequest.getDescription());
    }

    @Test
    public void getItemRequestByIdShouldReturnTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest1));

        ItemRequestDto resultRequest =
                itemRequestService.getItemRequestById(itemRequest1.getId(), user1.getId());

        assertEquals(itemRequest1.getId(), resultRequest.getId());
        assertEquals(itemRequest1.getRequestor(), resultRequest.getRequestor());
        assertEquals(itemRequest1.getDescription(), resultRequest.getDescription());
    }

    @Test
    public void getItemRequestByIdThrowsItemRequestNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));

        assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(itemRequest1.getId(), user1.getId()));
    }
}