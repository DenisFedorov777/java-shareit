package ru.practicum.shareit.exception;

public class ExistEmailException extends RuntimeException {

    public ExistEmailException(String message) {
        super(message);
    }
}