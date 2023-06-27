package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.statuses.BookingState;
import ru.practicum.shareit.booking.statuses.BookingStatus;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDtoResponse createBookingRequest(BookingDto bookingDto, Long userId) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new InvalidDataException("Дата начала бронирования должна быть раньше даты окончания.");
        }
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким идентификатором не найден."));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Товар с таким идентификатором не найден"));
        User userOwner = item.getOwner();
        if (Boolean.FALSE.equals(item.getAvailable())) {
            log.error("Товар недоступен для бронирования.");
            throw new InvalidDataException("Товар недоступен для бронирования");
        }
        if (!userOwner.getId().equals(userId)) {
            Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
            return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking), ItemMapper.toItemDto(item));
        } else {
            log.error("Владелец не может создать запрос на создание собственной вещи");
            throw new UserNotFoundException("Владелец не может создать запрос на создание собственной вещи");
        }
    }

    @Transactional
    @Override
    public BookingDtoResponse updateBookingStatusByOwner(Long bookingId, Long userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Такого бронирования нет."));
        ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());
        User userOwner = booking.getItem().getOwner();
        if (booking.getStatus().equals(BookingStatus.APPROVED) && approved) {
            log.info("Status already: approved");
            throw new InvalidDataException("Status already: approved");
        }
        if (booking.getStatus().equals(BookingStatus.REJECTED) && !approved) {
            log.info("Status already: rejected");
            throw new InvalidDataException("Статус - отклонен");
        }
        if (userId.equals(userOwner.getId())) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }

            return BookingMapper.toUpdateBookingDtoResponse(bookingRepository.save(booking), itemDto);
        } else {
            log.error("Только владелец может подтвердить бронирование предмета.");
            throw new UserNotFoundException("Только владелец может подтвердить бронирование предмета.");
        }
    }

    @Override
    public BookingDtoResponse getBookingDetails(Long bookingId, Long userId) {
        validateExistsUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено."));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new ItemNotFoundException("Товар с таким идентификатором не найден"));
        ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());
        User userOwner = booking.getItem().getOwner();
        User userAuthor = booking.getBooker();
        if (userId.equals(userAuthor.getId()) || userId.equals(userOwner.getId())) {
            return BookingMapper.toBookingDtoResponse(booking, itemDto);
        } else {
            log.error("Только автор или владелец может проверить детали бронирования.");
            throw new UserNotFoundException("Только автор или владелец может проверить детали бронирования.");
        }
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsByAuthor(BookingState state, Long userId) {
        validateExistsUser(userId);
        return getAllBookingsWithStateParameter(state, bookingRepository.findAllByBooker_IdOrderByStartDateDesc(userId))
                .stream()
                .map(booking -> {
                    ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());
                    return BookingMapper.toBookingDtoResponse(booking, itemDto);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getAllBookingByOwner(BookingState state, Long userId) {
        validateExistsUser(userId);
        List<Booking> listBookingsByOwner = bookingRepository.findAllByItem_Owner_IdOrderByStartDateDesc(userId);
        if (listBookingsByOwner.isEmpty()) {
            log.error("У этого пользователя нет добавленного товара.");
            throw new ItemNotFoundException("У этого пользователя нет добавленного товара.");
        }
        return getAllBookingsWithStateParameter(state, listBookingsByOwner)
                .stream()
                .map(booking -> {
                    ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());
                    return BookingMapper.toBookingDtoResponse(booking, itemDto);
                })
                .collect(Collectors.toList());
    }

    private List<Booking> getAllBookingsWithStateParameter(BookingState state, List<Booking> listBookingsByUser) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        switch (state) {
            case PAST:
                return getBookings(listBookingsByUser, booking -> booking.getEndDate().isBefore(currentDateTime) &&
                        booking.getStatus().equals(BookingStatus.APPROVED));
            case FUTURE:
                return getBookings(listBookingsByUser,
                        booking -> booking.getStartDate().isAfter(currentDateTime));
            case CURRENT:
                return getBookings(listBookingsByUser,
                        booking -> booking.getStartDate().isBefore(currentDateTime) &&
                                booking.getEndDate().isAfter(currentDateTime) &&
                                (booking.getStatus().equals(BookingStatus.APPROVED)
                                        || booking.getStatus().equals(BookingStatus.REJECTED)));
            case WAITING:
                return getBookings(listBookingsByUser, booking -> booking.getStatus().equals(BookingStatus.WAITING));
            case REJECTED:
                return getBookings(listBookingsByUser, booking -> booking.getStatus().equals(BookingStatus.REJECTED));
            default:
                return listBookingsByUser;
        }
    }

    private List<Booking> getBookings(List<Booking> listBookings,
                                      Predicate<Booking> predicate) {
        return listBookings.stream().filter(predicate).collect(Collectors.toList());
    }

    private void validateExistsUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("Пользователь с таким идентификатором не найден.");
            throw new UserNotFoundException("Пользователь с таким идентификатором не найден.");
        }
    }
}