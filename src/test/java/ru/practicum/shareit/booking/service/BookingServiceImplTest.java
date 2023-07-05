package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.statuses.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
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
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    User user1;
    User user2;
    Item item1;
    Item item2;
    Item item3;
    Comment comment1;
    Comment comment2;
    Booking booking1;
    Booking booking2;
    ItemRequest itemRequest1;
    LocalDateTime time = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        user1 = new User(1L, "TestUser", "testuser@email.com");
        user2 = new User(2L, "TestUser2", "testuser2@email.com");
        item1 = new Item(1L, "Item1", "Item1-Desc", true, user1, null);
        item2 = new Item(2L, "Item2", "Item2-Desc", true, user1, null);
        item3 = new Item(3L, "Item3", "Item3-Desc", true, user2, null);
        booking1 = new Booking(1L, user1, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item1, Status.WAITING);
        booking2 = new Booking(2L, user1, LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(10), item1, Status.WAITING);
        comment1 = new Comment(1L, item1, user1, LocalDateTime.now(), "Comentar");
        comment2 = new Comment(2L, item1, user1, LocalDateTime.now(), "Comentar2");
        itemRequest1 = new ItemRequest(1L, "RequestDesc", user1, LocalDateTime.now(), new ArrayList<>());
    }


    @Test
    public void getBookingShouldReturnBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking1));

        BookingResponseDto resultBooking = bookingService.getBooking(user1.getId(), booking1.getId());

        assertEquals(booking1.getId(), resultBooking.getId());
        assertEquals(booking1.getBooker(), resultBooking.getBooker());
        assertEquals(booking1.getItem(), resultBooking.getItem());
    }

    @Test
    public void getBookingShouldThrowBookingNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(user1.getId(), booking1.getId()));
    }

    @Test
    public void getBookingShouldThrowUnauthorizedAccessException() {
        booking1.setBooker(user2);
        booking1.getItem().setOwner(user2);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking1));

        assertThrows(UserBookingException.class,
                () -> bookingService.getBooking(user1.getId(), booking1.getId()));
    }

    @Test
    public void getAllBookingsByAllStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBooker_IdOrderByStartDesc(user1.getId(), pageable)).thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllBookings(user1.getId(), "ALL", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllBookingsByCurrentStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        any(), any(), any(), any()))
                .thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllBookings(user1.getId(), "CURRENT", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllBookingsByPastStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository
                .findAllByBooker_IdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllBookings(user1.getId(), "PAST", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllBookingsByFutureStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository
                .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllBookings(user1.getId(), "FUTURE", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllBookingsByWaitingStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),any(Status.class), any(Pageable.class)))
                .thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllBookings(user1.getId(), "WAITING", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllBookingsByRejectedStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllBookings(user1.getId(), "REJECTED", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllBookingsByUnknownStateTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));

        assertThrows(UnknownStatusException.class,
                () -> bookingService.getAllBookings(user1.getId(), "UNKNOWN", 0, 10));
    }

    @Test
    public void getAllOwnerBookingsByAllStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(user1.getId(), pageable)).thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllOwnerBookings(user1.getId(), "ALL", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllOwnerBookingsByCurrentStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any())).thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllOwnerBookings(user1.getId(), "CURRENT", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllOwnerBookingsByPastStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(anyLong(), any(), any())).thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllOwnerBookings(user1.getId(), "PAST", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllOwnerBookingsByFutureStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(
                anyLong(), any(), any())).thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllOwnerBookings(user1.getId(), "FUTURE", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllOwnerBookingsByWaitingStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(
                anyLong(), any(), any())).thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllOwnerBookings(user1.getId(), "WAITING", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllOwnerBookingsByRejectedStateTest() {
        int page = 0;
        int size = 10;
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        List<Booking> mockedBookings = List.of(booking1, booking2);
        Page<Booking> mockedBookingsPage = new PageImpl<>(mockedBookings, pageable, mockedBookings.size());

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(mockedBookingsPage);

        List<BookingResponseDto> resultBookList =
                bookingService.getAllOwnerBookings(user1.getId(), "REJECTED", 0, 10);

        assertEquals(2, resultBookList.size());
        assertEquals(booking1.getId(), resultBookList.get(0).getId());
        assertEquals(booking1.getBooker(), resultBookList.get(0).getBooker());
        assertEquals(booking2.getId(), resultBookList.get(1).getId());
        assertEquals(booking2.getBooker(), resultBookList.get(1).getBooker());
    }

    @Test
    public void getAllOwnerBookingsByUnsupportedStateTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));

        assertThrows(UnknownStatusException.class,
                () -> bookingService.getAllOwnerBookings(user1.getId(), "UNKNOWN", 0, 10));
    }

    @Test
    public void postBookingTestShouldCreate() {
        booking1.getItem().setOwner(user2);
        when(bookingRepository.save(any())).thenReturn(booking1);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));

        BookingResponseDto resultBooking =
                bookingService.postBooking(BookingMapper.toBookingDto(booking1), user1.getId());

        assertEquals(booking1.getId(), resultBooking.getId());
        assertEquals(booking1.getItem(), resultBooking.getItem());
        assertEquals(booking1.getBooker(), resultBooking.getBooker());
    }

    @Test
    public void postBookingShouldThrowUserNotFoundExceptionTest() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> bookingService.postBooking(BookingMapper.toBookingDto(booking1), user1.getId()));
    }

    @Test
    public void postBookingShouldThrowItemNotFoundExceptionTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.postBooking(BookingMapper.toBookingDto(booking1), user1.getId()));
    }

    @Test
    public void postBookingWithUnavailableItemTest() {
        booking1.getItem().setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.postBooking(BookingMapper.toBookingDto(booking1), user1.getId()));
    }

    @Test
    public void postBookingShouldThrowUnauthorizedAccessExceptionTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));

        assertThrows(UserBookingException.class,
                () -> bookingService.postBooking(BookingMapper.toBookingDto(booking1), user1.getId()));
    }

    @Test
    public void postBookingWithIncorrectStartDateTest() {
        booking1.setStart(LocalDateTime.now().plusDays(20));
        booking1.getItem().setOwner(user2);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.postBooking(BookingMapper.toBookingDto(booking1), user1.getId()));
    }

    @Test
    public void approveBookingTestShouldApprove() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking1));
        when(bookingRepository.save(any())).thenReturn(booking1);

        BookingResponseDto resultBooking =
                bookingService.approveBooking(user1.getId(), booking1.getId(), true);

        assertEquals(Status.APPROVED, resultBooking.getStatus());
        assertEquals(booking1.getId(), resultBooking.getId());
        assertEquals(booking1.getStart(), resultBooking.getStart());
    }

    @Test
    public void approveBookingTestShouldReject() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking1));
        when(bookingRepository.save(any())).thenReturn(booking1);

        BookingResponseDto resultBooking =
                bookingService.approveBooking(user1.getId(), booking1.getId(), false);

        assertEquals(Status.REJECTED, resultBooking.getStatus());
        assertEquals(booking1.getId(), resultBooking.getId());
        assertEquals(booking1.getStart(), resultBooking.getStart());
    }

    @Test
    public void approveBookingTestAlreadyBooked() {
        booking1.setStatus(Status.APPROVED);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking1));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.approveBooking(user1.getId(), booking1.getId(), true));
    }

    @Test
    public void approveBookingNotOwnerTest() {
        booking1.getItem().setOwner(user2);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking1));

        assertThrows(UserBookingException.class,
                () -> bookingService.approveBooking(user1.getId(), booking1.getId(), true));
    }
}