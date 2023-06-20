package ru.practicum.shareit.exception;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Generated
public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message) {
        super(message);
        log.warn(message);
    }
}