package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end,
                                                              Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start, LocalDateTime end,
                                                                 Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    Optional<Booking> findOneByItemIdAndBookerIdAndEndBeforeAndStatus(Long itemId, Long bookerId, LocalDateTime end,
                                                                      BookingStatus status);

    @Query("select new ru.practicum.shareit.booking.model.BookingShort(i.id, MAX(b.end))" +
            "from Booking as b " +
            "LEFT JOIN  b.item AS i " +
            "JOIN  i.owner AS ow " +
            "where b.end < ?1 and ow.id = ?2 " +
            "group by i.id")
    List<BookingShort> getLastBookingByOwnerId(LocalDateTime date, Long ownerId);

    @Query("select new ru.practicum.shareit.booking.model.BookingShort(i.id, MIN(b.start))" +
            "from Booking as b " +
            "LEFT JOIN  b.item AS i " +
            "JOIN  i.owner AS ow " +
            "where b.start > ?1 and ow.id = ?2 " +
            "group by i.id")
    List<BookingShort> getNextBookingByOwnerId(LocalDateTime date, Long ownerId);
}

