package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    public void testSerialize() throws Exception {
        final ItemRequestDto userDto = new ItemRequestDto(1L, "descr", null, null,
                null);
        JsonContent<ItemRequestDto> json = jacksonTester.write(userDto);
        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("descr");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"description\":\"descr\"}";
        ItemRequestDto dto = jacksonTester.parseObject(json);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getDescription()).isEqualTo("descr");
    }
}
