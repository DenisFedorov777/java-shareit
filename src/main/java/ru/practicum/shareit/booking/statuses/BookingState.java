package ru.practicum.shareit.booking.statuses;

import lombok.Generated;

@Generated
public enum BookingState {
    ALL("ALL"),
    CURRENT("CURRENT"),
    PAST("PAST"),
    FUTURE("FUTURE"),
    WAITING("REJECTED"),
    REJECTED("WAITING");

    private final String state;

    BookingState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}