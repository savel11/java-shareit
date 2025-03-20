package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class NewItemDtoTest {
    @Autowired
    private JacksonTester<NewItemDto> jacksonTester;

    @Test
    public void testSerialize() throws Exception {
        final NewItemDto itemDto = new NewItemDto("item", "description", true, 1L);
        JsonContent<NewItemDto> result = jacksonTester.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"name\":\"name\",\"description\":\"descr\",\"available\":true}";
        NewItemDto dto = jacksonTester.parseObject(json);
        assertThat(dto.getName()).isEqualTo("name");
        assertThat(dto.getDescription()).isEqualTo("descr");
    }
}
