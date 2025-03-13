package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.error.exception.NotFoundException;

public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELED;

    public static BookingStatus getBookingStatus(String str) {
        BookingStatus bookingStatus;
        bookingStatus = switch (str) {
            case "Waiting", "WAITING" -> BookingStatus.WAITING;
            case "Approved", "APPROVED" -> BookingStatus.APPROVED;
            case "Rejected", "REJECTED" -> BookingStatus.REJECTED;
            case "Canceled", "CANCELED" -> BookingStatus.CANCELED;
            default -> throw new NotFoundException("Статус бронирование не найден");
        };
        return bookingStatus;
    }

    public static String getStringBookingStatus(BookingStatus bookingStatus) {
        String str;
        str = switch (bookingStatus) {
            case WAITING -> "WAITING";
            case APPROVED -> "APPROVED";
            case REJECTED -> "REJECTED";
            case CANCELED -> "CANCELED";
        };
        return str;
    }
}
