package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exception.InvalidFormatException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public BookingDto create(NewBookingDto newBookingDto, Long bookerId) {
        log.info("Пользоваетль с id = " + bookerId + " создает заявку на бронирование вещи с id = "
                + newBookingDto.getItemId());
        User booker = checkAndGetUser(bookerId);
        Item item = checkAndGetItem(newBookingDto.getItemId());
        log.trace("Проверка доступности вещи для бронирования");
        if (!item.getAvailable()) {
            log.warn("Вещь недоступна!");
            throw new InvalidFormatException("Вещь недоступна для бронирования");
        }
        log.trace("Проверка корректности дат бронирования");
        if (newBookingDto.getStart().isAfter(newBookingDto.getEnd())) {
            log.warn("Конец бронирования не может быть раньше даты начала бронирования");
            throw new InvalidFormatException("Дата начала бронирования должна быть раньше чем дата конца бронирования!");
        }
        log.trace("Даты корректны");
        log.info("Запрос создан!");
        Booking booking = BookingMapper.toBooking(newBookingDto, item, booker);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        log.info("Пользователь с id = " + userId + " начинает процесс подтверждения бронирования с id = " + bookingId);
        Booking booking = checkAndGetBooking(bookingId);
        log.trace("Проверка актуальности брони");
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            log.warn("Бронь не актуальна: Время бронирование истекло");
            throw new InvalidFormatException("Нельзя подтвердить бронь, время которой истекло!");
        }
        log.trace("Проверка существования пользователя");
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Пользователь с id = " + userId + " не найден!");
            throw new InvalidFormatException("Пользователя с id = " + userId + " не существует");
        }
        log.trace("Проверка закончена: Пользователь существует");
        User owner = optionalUser.get();
        log.trace("Проверка прав пользователя");
        if (!owner.getId().equals(booking.getItem().getOwner().getId())) {
            log.warn("У пользователя нет прав на подтверждение бронирования: пользоваетель с id = " + userId + " не является " +
                    "хозяином вещи");
            throw new InvalidFormatException("Подтверждать бронирование может только хозяин вещи");
        }
        log.trace("Пользователь обладает правами на подтверждение");
        if (approved) {
            log.info("Бронирование подтвержденно!");
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            log.info("Бронирование отклоненно!");
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long bookingId, Long userId) {
        log.info("Пользоваетлья с id = " + userId + " запрашивает просмотр брони с id = " + bookingId);
        Booking booking = checkAndGetBooking(bookingId);
        checkAndGetUser(userId);
        log.trace("Проверка прав пользователя");
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("У пользоваетеля нет прав на просмотр брони: Бронь может смотреть только хозяин вещи и бронирующий " +
                    " человек");
            throw new NotOwnerException("Бронь может смотреть только хозяин вещи и бронирующий человек");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBooking(Long userId, String state) {
        log.info("Пользователь с id = " + userId + " просматривает свои брони");
        checkAndGetUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        bookings = switch (BookingState.getBookingState(state)) {
            case BookingState.ALL -> bookingRepository.findByBookerId(userId, sort);
            case BookingState.CURRENT ->
                    bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                            LocalDateTime.now(), sort);
            case BookingState.FUTURE -> bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(),
                    sort);
            case BookingState.PAST -> bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort);
            case BookingState.WAITING -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case BookingState.REJECTED ->
                    bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
        };
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingOwner(Long userId, String state) {
        log.info("Пользователь с id = " + userId + " просматривает  брони на свои вещи");
        checkAndGetUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        bookings = switch (BookingState.getBookingState(state)) {
            case BookingState.ALL -> bookingRepository.findByItemOwnerId(userId, sort);
            case BookingState.CURRENT ->
                    bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                            LocalDateTime.now(), sort);
            case BookingState.FUTURE -> bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(),
                    sort);
            case BookingState.PAST ->
                    bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), sort);
            case BookingState.WAITING ->
                    bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case BookingState.REJECTED ->
                    bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
        };
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }


    private Booking checkAndGetBooking(Long bookingId) {
        log.trace("Проверка существования брони");
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            log.warn("Бронь с id = " + bookingId + " не найдена!");
            throw new NotFoundException("Брони с id = " + bookingId + " не существует");
        }
        log.trace("Проверка закончена: Бронь существует");
        return optionalBooking.get();
    }


    private User checkAndGetUser(Long userId) {
        log.trace("Проверка существования пользователя");
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Пользователь с id = " + userId + " не найден!");
            throw new NotFoundException("Пользователя с id = " + userId + " не существует");
        }
        log.trace("Проверка закончена: Пользователь существует");
        return optionalUser.get();
    }

    private Item checkAndGetItem(Long itemId) {
        log.trace("Проверка существования вещи");
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            log.warn("Вещь с id = " + itemId + " не найдена!");
            throw new NotFoundException("Вещь с id = " + itemId + " не существует");
        }
        log.trace("Проверка закончена: Вещь существует");
        return optionalItem.get();
    }
}
