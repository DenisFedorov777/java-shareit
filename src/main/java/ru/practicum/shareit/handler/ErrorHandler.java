package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("Кажется Вы ошиблись, здесь пусто {}", e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class,
            BookingNotFoundException.class, ItemRequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFound(final RuntimeException e) {
        log.warn("Кажется Вы ошиблись, здесь пусто {}", e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String unauthorizedAccessException(final UserBookingException e) {
        log.warn("Кажется Вы ошиблись, здесь пусто {}", e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("Ошибочка вышла {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String validationConstraintException(final ConstraintViolationException e) {
        log.warn("Ошибочка вышла {}", e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String validationNoSuchElementException(final NoSuchElementException e) {
        log.warn("Кажется Вы ошиблись, здесь пусто {}", e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String entityNotFoundExceptionException(final EntityNotFoundException e) {
        log.warn("Кажется Вы ошиблись, здесь пусто {}", e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedStatusException(UnknownStatusException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String deniedCommentingException(final UserCommentingException e) {
        log.warn("Ошибочка вышла {}", e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String unHandledException(final Exception e) {
        log.warn("Сервер полетел {}", e.getMessage(), e);
        return e.getMessage();
    }
}