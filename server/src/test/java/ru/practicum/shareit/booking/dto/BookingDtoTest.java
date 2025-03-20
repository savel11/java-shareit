package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jacksonTester;

    @Test
    public void testSerialize() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "item", "description", true, 1L,
                null);
        UserDto userDto = new UserDto(1L, "sav", "sav@mail.ru");
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);
        final BookingDto bookItemRequestDto = new BookingDto(1L, itemDto, start, end, userDto,
                BookingStatus.getStringBookingStatus(BookingStatus.APPROVED));
        JsonContent<BookingDto> result = jacksonTester.write(bookItemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"start\":\"2024-11-23T11:06:20\",\"end\":\"2024-12-22T21:04:20\",\"status\":\"WAITING\"}";
        BookingDto dto = jacksonTester.parseObject(json);
        assertThat(dto.getId()).isEqualTo(1);
    }

}