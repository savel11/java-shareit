package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.error.exception.NotFoundException;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState getBookingState(String str) {
        BookingState bookingState;
        bookingState = switch (str) {
            case "ALL", "All" -> BookingState.ALL;
            case "CURRENT", "Current" -> BookingState.CURRENT;
            case "PAST", "Past" -> BookingState.PAST;
            case "FUTURE", "Future" -> BookingState.FUTURE;
            case "WAITING", "Waiting" -> BookingState.WAITING;
            case "REJECTED", "Rejected" -> BookingState.REJECTED;
            default -> throw new NotFoundException("Параметр не найден");
        };
        return bookingState;
    }

    public static String getStringBookingState(BookingState state) {
        String str;
        str = switch (state) {
            case ALL -> "ALL";
            case CURRENT -> "CURRENT";
            case PAST -> "PAST";
            case FUTURE -> "FUTURE";
            case WAITING -> "WAITING";
            case REJECTED -> "REJECTED";
        };
        return str;
    }
}
