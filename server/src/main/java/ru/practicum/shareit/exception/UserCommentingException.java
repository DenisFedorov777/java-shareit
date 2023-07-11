package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserCommentingException extends RuntimeException {
    public UserCommentingException(String message) {
        super(message);
        log.warn(message);
    }
}