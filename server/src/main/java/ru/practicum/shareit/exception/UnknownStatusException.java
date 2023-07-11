package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnknownStatusException extends RuntimeException {

    public UnknownStatusException(String message) {
        super(message);
        log.warn(message);
    }
}