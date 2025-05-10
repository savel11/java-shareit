package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class NewBookingDtoTest {
    @Autowired
    private JacksonTester<NewBookingDto> jacksonTester;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);
        final NewBookingDto booking = new NewBookingDto(1L, start, end);
        JsonContent<NewBookingDto> result = jacksonTester.write(booking);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"itemId\":1,\"start\":\"2024-11-23T11:06:20\",\"end\":\"2024-12-22T21:04:20\"}";
        NewBookingDto booking = jacksonTester.parseObject(json);
        assertThat(booking.getItemId()).isEqualTo(1);
    }
}
