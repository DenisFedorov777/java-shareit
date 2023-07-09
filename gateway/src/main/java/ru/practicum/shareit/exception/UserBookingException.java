package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserBookingException extends RuntimeException {

    public UserBookingException(String message) {
        super(message);
        log.warn(message);
    }
}
