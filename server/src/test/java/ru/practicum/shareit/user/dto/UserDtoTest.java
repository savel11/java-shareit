package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;


import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    public void testSerialize() throws Exception {
        final UserDto userDto = new UserDto(1L, "Saveliy", "sav@gmail.com");
        JsonContent<UserDto> result = jacksonTester.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Saveliy");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("sav@gmail.com");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"Saveliy\",\"email\":\"sav@gmail.com\"}";
        UserDto dto = jacksonTester.parseObject(json);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Saveliy");
        assertThat(dto.getEmail()).isEqualTo("sav@gmail.com");
    }
}
