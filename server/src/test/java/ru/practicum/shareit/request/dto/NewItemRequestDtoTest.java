package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class NewItemRequestDtoTest {
    @Autowired
    private JacksonTester<NewItemRequestDto> jacksonTester;

    @Test
    public void testSerialize() throws Exception {
        final NewItemRequestDto userDto = new NewItemRequestDto("descr");
        JsonContent<NewItemRequestDto> json = jacksonTester.write(userDto);
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("descr");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"description\":\"descr\"}";
        NewItemRequestDto dto = jacksonTester.parseObject(json);
        assertThat(dto.getDescription()).isEqualTo("descr");
    }
}
