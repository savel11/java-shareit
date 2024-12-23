package ru.practicum.shareit.booking;

import ru.practicum.shareit.error.exception.NotFoundException;

public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELED;

    public static BookingStatus getBookingStatus(String str) {
        BookingStatus bookingStatus;
        bookingStatus = switch (str) {
            case "Waiting", "waiting" -> BookingStatus.WAITING;
            case "Approved", "approved" -> BookingStatus.APPROVED;
            case "Rejected", "rejected" -> BookingStatus.REJECTED;
            case "Canceled", "canceled" -> BookingStatus.CANCELED;
            default -> throw new NotFoundException("Статус бронирование не найден");
        };
        return bookingStatus;
    }

    public static String getStringBookingStatus(BookingStatus bookingStatus) {
        String str;
        str = switch (bookingStatus) {
            case WAITING -> "Waiting";
            case APPROVED -> "Approved";
            case REJECTED -> "Rejected";
            case CANCELED -> "Canceled";
        };
        return str;
    }
}
