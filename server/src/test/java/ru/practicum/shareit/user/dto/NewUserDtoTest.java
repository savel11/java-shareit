package ru.practicum.shareit.user.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class NewUserDtoTest {
    @Autowired
    private JacksonTester<NewUserDto> jacksonTester;

    @Test
    public void testSerialize() throws Exception {
        final NewUserDto userDto = new NewUserDto("Saveliy", "sav@gmail.com");
        JsonContent<NewUserDto> result = jacksonTester.write(userDto);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Saveliy");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("sav@gmail.com");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"name\":\"Saveliy\",\"email\":\"sav@gmail.com\"}";
        NewUserDto dto = jacksonTester.parseObject(json);
        assertThat(dto.getName()).isEqualTo("Saveliy");
        assertThat(dto.getEmail()).isEqualTo("sav@gmail.com");
    }
}

