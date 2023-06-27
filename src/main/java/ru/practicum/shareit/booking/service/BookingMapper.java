package ru.practicum.shareit.booking.service;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.dto.BookingOwnerDto;
import ru.practicum.shareit.booking.statuses.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public Booking toBooking(BookingDto bookingDto, Item item, User booker) {

        return Booking.builder()
                .startDate(bookingDto.getStart())
                .endDate(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public BookingDtoResponse toBookingDtoResponse(Booking booking, ItemDto itemDto) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .item(itemDto)
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .booker(UserMapper.toBookerDto(booking.getBooker()))
                .build();
    }

    public BookingDtoResponse toUpdateBookingDtoResponse(Booking booking, ItemDto itemDto) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .item(itemDto)
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .booker(UserMapper.toBookerDto(booking.getBooker()))
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
}