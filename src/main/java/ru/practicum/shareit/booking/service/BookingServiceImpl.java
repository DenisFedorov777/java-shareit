package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.statuses.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.statuses.Status.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto getBooking(Long ownerId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        User booker = booking.getBooker();
        User itemOwner = booking.getItem().getOwner();
        if (!booker.getId().equals(ownerId)
                && !itemOwner.getId().equals(ownerId)) {
            log.warn("Пользователь не является создателем вещи или бронирования");
            throw new UserBookingException("Пользователь не является создателем вещи или бронирования");
        }
        log.debug("Получение бронирования: " + booking);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookings(Long ownerId, String state, Integer page, Integer size) {
        findUser(ownerId);
        log.debug("Получение бронирований по id владельца и состоянию: " + state);
        int adjustedPage = (page + size - 1) / size;
        Page<Booking> bookingPage;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        switch (state) {
            case "ALL":
                bookingPage = bookingRepository.findByBooker_IdOrderByStartDesc(ownerId, pageable);
                break;
            case "CURRENT":
                bookingPage = bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookingPage = bookingRepository
                        .findAllByBooker_IdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookingPage = bookingRepository
                        .findAllByBookerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookingPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(ownerId, WAITING, pageable);
                break;
            case "REJECTED":
                bookingPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(ownerId, REJECTED, pageable);
                break;
            default:
                throw new UnknownStatusException("Unknown state: " + state);
        }
        List<BookingResponseDto> bookingDtos = bookingPage.map(BookingMapper::toBookingResponseDto).getContent();
        return bookingDtos;
    }

    @Override
    public List<BookingResponseDto> getAllOwnerBookings(Long ownerId, String state, Integer page, Integer size) {
        findUser(ownerId);
        log.debug("Получение бронирований по id владельца и состоянию: " + state);
        int adjustedPage = (page + size - 1) / size;
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by("id").ascending());
        Page<Booking> bookingPage;

        switch (state) {
            case "ALL":
                bookingPage = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId, pageable);
                break;
            case "CURRENT":
                bookingPage = bookingRepository
                        .findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                                ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookingPage = bookingRepository
                        .findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookingPage = bookingRepository
                        .findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookingPage = bookingRepository
                        .findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, WAITING, pageable);
                break;
            case "REJECTED":
                bookingPage = bookingRepository
                        .findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, REJECTED, pageable);
                break;
            default:
                throw new UnknownStatusException("Unknown state: " + state);
        }
        List<BookingResponseDto> bookingDtos = bookingPage.map(BookingMapper::toBookingResponseDto).getContent();
        return bookingDtos;
    }

    @Transactional
    @Override
    public BookingResponseDto postBooking(BookingDto bookingDto, Long ownerId) {
        User booker = findUser(ownerId);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new ItemNotFoundException("Вещь не найдена"));
        if (!item.getAvailable()) {
            log.warn("Вещь недоступна для бронирования");
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(ownerId)) {
            log.warn("Нельзя бронировать вещь у себя");
            throw new UserBookingException("Нельзя бронировать вещь у себя");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            log.warn("Некорректные даты бронирования");
            throw new IllegalArgumentException("Некорректные даты бронирования");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(WAITING);
        booking = bookingRepository.save(booking);
        log.debug("Сохранение бронирования " + booking);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Transactional
    @Override
    public BookingResponseDto approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        findUser(ownerId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        Status status = booking.getStatus();
        if (status.toString().equals("APPROVED")) {
            log.warn("Бронирование уже подтверждено");
            throw new IllegalArgumentException("Бронирование уже подтверждено");
        }
        User user = booking.getItem().getOwner();
        if (!user.getId().equals(ownerId)) {
            log.warn("Пользователь не является создателем вещи ");
            throw new UserBookingException("Пользователь не является создателем вещи ");
        }
        if (approved) {
            log.debug("Подтверждение бронирования id=" + bookingId + ", статус APPROVED");
            booking.setStatus(APPROVED);
        } else {
            log.debug("Подтверждение бронирования id=" + bookingId + ", статус REJECTED");
            booking.setStatus(REJECTED);
        }
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    private User findUser(Long ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }
}