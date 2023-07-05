package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.statuses.Status;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserCommentingException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    User user1;
    Item item1;
    Item item2;
    Comment comment1;
    Comment comment2;
    Booking booking1;
    Booking booking2;
    ItemRequest itemRequest1;
    LocalDateTime time = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        user1 = new User(1L, "TestUser", "testuser@email.com");
        item1 = new Item(1L, "Item1", "Item1-Desc", true, user1, null);
        item2 = new Item(2L, "Item2", "Item2-Desc", true, user1, null);
        booking1 = new Booking(1L, user1, time.minusDays(2),
                time.minusDays(1), item1, Status.WAITING);
        booking2 = new Booking(2L, user1, time.plusHours(5),
                time.plusHours(10), item1, Status.WAITING);
        comment1 = new Comment(1L, item1, user1, time, "Comentar");
        comment2 = new Comment(2L, item1, user1, time, "Comentar2");
        itemRequest1 = new ItemRequest(1L, "RequestDesc", user1, time, new ArrayList<>());
    }

    @Test
    public void getItemsShouldReturnList() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<Item> mockedItems = List.of(item1, item2);
        Page<Item> mockedItemPage = new PageImpl<>(mockedItems, pageable, mockedItems.size());

        when(itemRepository.findByOwnerId(user1.getId(), pageable)).thenReturn(mockedItemPage);
        when(bookingRepository.findAllByItem_IdAndItem_Owner_IdAndStartBeforeAndStatusNotOrderByStartDesc(
                anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findAllByItem_IdAndItem_Owner_IdAndStartAfterAndStatusNotOrderByStartAsc(
                anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(booking2));

        List<ItemDtoWithBooking> itemDtos = itemService.getItems(user1.getId(), page, size);

        assertEquals(itemDtos.size(), 2);
        assertEquals(itemDtos.get(0).getId(), item1.getId());
        assertEquals(itemDtos.get(1).getId(), item2.getId());
        assertEquals(itemDtos.get(0).getLastBooking().getId(), booking1.getId());
        assertEquals(itemDtos.get(0).getNextBooking().getId(), booking2.getId());
    }

    @Test
    public void getItemByIdShouldReturnItem() {
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findAllByItem_IdAndItem_Owner_IdAndStartBeforeAndStatusNotOrderByStartDesc(
                anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findAllByItem_IdAndItem_Owner_IdAndStartAfterAndStatusNotOrderByStartAsc(
                anyLong(), anyLong(), any(), any())).thenReturn(List.of(booking2));
        when(commentRepository.findAllByItem_Id(item1.getId())).thenReturn(List.of(comment1));

        ItemDtoWithBooking resultItem = itemService.getItemById(item1.getId(), user1.getId());

        assertEquals(item1.getId(), resultItem.getId());
        assertEquals(item1.getName(), resultItem.getName());
        assertEquals(item1.getOwner().getId(), user1.getId());
        assertEquals(booking1.getId(), resultItem.getLastBooking().getId());
    }

    @Test
    public void postItemShouldCreate() {
        item1.setRequest(itemRequest1);
        when(itemRepository.save(any())).thenReturn(item1);
        when(itemRequestRepository.getReferenceById(any())).thenReturn(itemRequest1);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));

        ItemDto resultItem = itemService.postItem(ItemMapper.toItemDto(item1), user1.getId());

        assertEquals(item1.getId(), resultItem.getId());
        assertEquals(item1.getName(), resultItem.getName());
        assertEquals(item1.getOwner(), user1);
        assertEquals(item1.getRequest(), itemRequest1);
    }

    @Test
    public void updateItemShouldUpdate() {
        item1.setRequest(itemRequest1);
        when(itemRepository.save(any())).thenReturn(item1);
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));
        when(itemRequestRepository.getReferenceById(any())).thenReturn(itemRequest1);

        ItemDto resultItem = itemService.updateItem(ItemMapper.toItemDto(item1), user1.getId());

        assertEquals(item1.getId(), resultItem.getId());
        assertEquals(item1.getName(), resultItem.getName());
        assertEquals(item1.getOwner(), user1);
        assertEquals(item1.getRequest(), itemRequest1);
    }

    @Test
    public void updateItemShouldThrowItemNotFound() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(ItemMapper.toItemDto(item1), user1.getId()));
    }

    @Test
    public void updateItemShouldThrowRuntimeException() {
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));

        assertThrows(RuntimeException.class,
                () -> itemService.updateItem(ItemMapper.toItemDto(item1), 2L));
    }

    @Test
    public void deleteItemShouldDeleteItemById() {
        itemService.deleteItem(item1.getId());

        verify(itemRepository).deleteById(item1.getId());
    }

    @Test
    public void searchItemsShouldReturnEmptyList() {
        List<ItemDto> resultList = itemService.searchItems("", 0, 10);

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void searchItemsShouldReturnList() {
        Page<Item> itemsPage = new PageImpl<>(List.of(item1, item2));

        when(itemRepository.searchByText("text",
                PageRequest.of(0, 10, Sort.by("name").ascending()))).thenReturn(itemsPage);

        List<ItemDto> itemsList = itemService.searchItems("text", 0, 10);

        assertEquals(2, itemsList.size());
        assertEquals(itemsList.get(0), ItemMapper.toItemDto(item1));
        assertEquals(itemsList.get(1), ItemMapper.toItemDto(item2));
    }

    @Test
    public void postCommentShouldPost() {
        when(bookingRepository.findAllByItem_IdAndBooker_IdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn((List.of(booking1, booking2)));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        when(commentRepository.save(any())).thenReturn(comment1);

        CommentDto resultComment = itemService.postComment(item1.getId(), comment1, user1.getId());

        assertEquals(comment1.getId(), resultComment.getId());
        assertEquals(comment1.getAuthor().getName(), resultComment.getAuthorName());
        assertEquals(comment1.getText(), resultComment.getText());
    }

    @Test
    public void postCommentShouldThrowDeniedCommentingException() {
        when(bookingRepository.findAllByItem_IdAndBooker_IdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn((Collections.emptyList()));

        assertThrows(UserCommentingException.class,
                () -> itemService.postComment(item1.getId(), comment1, user1.getId()));
    }

    @Test
    public void postCommentShouldThrowUserNotFoundException() {
        when(bookingRepository.findAllByItem_IdAndBooker_IdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn((List.of(booking1, booking2)));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> itemService.postComment(item1.getId(), comment1, user1.getId()));
    }

    @Test
    public void postCommentShouldThrowItemNotFoundException() {
        when(bookingRepository.findAllByItem_IdAndBooker_IdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn((List.of(booking1, booking2)));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> itemService.postComment(item1.getId(), comment1, user1.getId()));
    }

    @Test
    public void searchCommentsShouldReturnList() {
        when(commentRepository.findAllByItem_Id(item1.getId())).thenReturn(List.of(comment1));
        when(commentRepository.findAllByAuthor_Id(user1.getId())).thenReturn(List.of(comment1, comment2));
        when(commentRepository.findByTextContainingIgnoreCase(any())).thenReturn(List.of(comment1, comment2));

        List<CommentDto> commentList = itemService.searchComments(item1.getId(), user1.getId(), "text");

        assertEquals(2, commentList.size());
    }

    @Test
    public void searchCommentsShouldReturnListWithEmptyItemId() {
        when(commentRepository.findAllByAuthor_Id(user1.getId())).thenReturn(List.of(comment1, comment2));
        when(commentRepository.findByTextContainingIgnoreCase(any())).thenReturn(List.of(comment1, comment2));

        List<CommentDto> commentList = itemService.searchComments(null, user1.getId(), "text");

        assertEquals(2, commentList.size());
    }

    @Test
    public void searchCommentsShouldReturnListWithEmptyuserId() {
        when(commentRepository.findAllByItem_Id(item1.getId())).thenReturn(List.of(comment1));
        when(commentRepository.findByTextContainingIgnoreCase(any())).thenReturn(List.of(comment1, comment2));

        List<CommentDto> commentList = itemService.searchComments(item1.getId(), null, "text");

        assertEquals(2, commentList.size());
    }

    @Test
    public void searchCommentsShouldReturnListWithEmptyText() {
        when(commentRepository.findAllByItem_Id(item1.getId())).thenReturn(List.of(comment1));
        when(commentRepository.findAllByAuthor_Id(user1.getId())).thenReturn(List.of(comment1, comment2));

        List<CommentDto> commentList = itemService.searchComments(item1.getId(), user1.getId(), null);

        assertEquals(2, commentList.size());
    }
}