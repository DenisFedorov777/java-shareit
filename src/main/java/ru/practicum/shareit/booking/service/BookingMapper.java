package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.dto.BookingOwnerDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.statuses.BookingStatus;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public Booking toBooking(BookingDto bookingDto, Long userId) {
        if (!isStartBeforeEnd(bookingDto)) {
            throw new InvalidDataException("Дата начала бронирования должна быть раньше даты окончания.");
        }
        return Booking.builder()
                .startDate(bookingDto.getStart())
                .endDate(bookingDto.getEnd())
                .item(itemRepository.findById(bookingDto.getItemId())
                        .orElseThrow(() -> new ItemNotFoundException("Item not found for create booking")))
                .booker(userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found for create booking")))
                .status(BookingStatus.WAITING)
                .build();
    }

    public BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .item(itemMapper.toItemDto(booking.getItem()))
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .booker(userMapper.toBookerDto(booking.getBooker()))
                .build();
    }

    public BookingDtoResponse toUpdateBookingDtoResponse(Booking booking) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .item(itemMapper.toItemDto(booking.getItem()))
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .booker(userMapper.toBookerDto(booking.getBooker()))
                .build();
    }

    public BookingOwnerDto toBookingOwnerDto(Booking booking) {
        return BookingOwnerDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    private boolean isStartBeforeEnd(BookingDto bookingDto) {
        return bookingDto.getStart().isBefore(bookingDto.getEnd());
    }
}