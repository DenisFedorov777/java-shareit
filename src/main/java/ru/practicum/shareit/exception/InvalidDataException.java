package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidDataException extends RuntimeException {

    public InvalidDataException(String message) {
        super(message);
        log.warn(message);
    }
}