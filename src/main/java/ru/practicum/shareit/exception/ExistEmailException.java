package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExistEmailException extends RuntimeException {

    public ExistEmailException(String message) {
        super(message);
        log.warn(message);
    }
}