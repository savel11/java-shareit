package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exception.InvalidFormatException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager entityManager;

    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userDto = userService.create(new NewUserDto("Saveliy", "saveliy.losev@gmail.com"));
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        itemDto = itemService.create(newItem, userDto.getId());
    }

    @Test
    void create() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusNanos(100000000);
        NewBookingDto newBookingDto = new NewBookingDto(itemDto.getId(), start, end);
        BookingDto bookingDto = bookingService.create(newBookingDto, userDto.getId());
        TypedQuery<Booking> query = entityManager.createQuery("SELECT b from Booking as b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDto.getId()).getSingleResult();
        assertThat(booking, notNullValue());
        assertThat(booking.getBooker().getName(), equalTo(userDto.getName()));
        assertThat(booking.getItem().getName(), equalTo(itemDto.getName()));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
    }

    @Test
    void createWithNotExistUser() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusNanos(100000000);
        NewBookingDto newBookingDto = new NewBookingDto(itemDto.getId(), start, end);
        assertThrows(NotFoundException.class, () -> bookingService.create(newBookingDto, 100L));
    }

    @Test
    void createWithNotAvailable() {
        NewItemDto newItem = new NewItemDto("nam", "descrip", false, null);
        ItemDto item = itemService.create(newItem, userDto.getId());
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusNanos(100000000);
        NewBookingDto newBookingDto = new NewBookingDto(item.getId(), start, end);
        assertThrows(InvalidFormatException.class, () -> bookingService.create(newBookingDto, userDto.getId()));
    }

    @Test
    void createWithNotCorrectDate() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        NewBookingDto newBookingDto = new NewBookingDto(itemDto.getId(), start, end);
        assertThrows(InvalidFormatException.class, () -> bookingService.create(newBookingDto, userDto.getId()));
    }

    @Test
    void approve() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusNanos(100000000);
        NewBookingDto newBookingDto = new NewBookingDto(itemDto.getId(), start, end);
        BookingDto bookingDto = bookingService.create(newBookingDto, userDto.getId());
        BookingDto result = bookingService.approve(bookingDto.getId(), userDto.getId(), true);
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingDto.getId()));
        assertThat(result.getStatus(), equalTo("APPROVED"));
    }

    @Test
    void approveNotOwner() {
        UserDto user = userService.create(new NewUserDto("Saiy", "siy.losev@gmail.com"));
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusNanos(100000000);
        NewBookingDto newBookingDto = new NewBookingDto(itemDto.getId(), start, end);
        BookingDto bookingDto = bookingService.create(newBookingDto, userDto.getId());
        assertThrows(InvalidFormatException.class, () -> bookingService.approve(bookingDto.getId(), user.getId(), true));
    }

    @Test
    void approveWithNotCorrectDate() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusNanos(100000);
        NewBookingDto newBookingDto = new NewBookingDto(itemDto.getId(), start, end);
        BookingDto bookingDto = bookingService.create(newBookingDto, userDto.getId());
        assertThrows(InvalidFormatException.class, () -> bookingService.approve(bookingDto.getId(), userDto.getId(), true));
    }

    @Test
    void getById() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusNanos(100000000);
        NewBookingDto newBookingDto = new NewBookingDto(itemDto.getId(), start, end);
        BookingDto bookingDto = bookingService.create(newBookingDto, userDto.getId());
        BookingDto result = bookingService.getById(bookingDto.getId(), userDto.getId());
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingDto.getId()));
        assertThat(result.getBooker(), equalTo(userDto));
    }

    @Test
    void getByIdNotOwner() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusNanos(100000000);
        NewBookingDto newBookingDto = new NewBookingDto(itemDto.getId(), start, end);
        BookingDto bookingDto = bookingService.create(newBookingDto, userDto.getId());
        UserDto user = userService.create(new NewUserDto("Saiy", "siy.losev@gmail.com"));
        assertThrows(NotOwnerException.class, () -> bookingService.getById(bookingDto.getId(), user.getId()));
    }
}
