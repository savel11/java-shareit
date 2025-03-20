package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Test
    public void testSerialize() throws Exception {
        final ItemDto itemDto = new ItemDto(1L, "item", "description", true, 1L,
                null);
        JsonContent<ItemDto> result = jacksonTester.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"name\",\"description\":\"descr\",\"available\":true}";
        ItemDto dto = jacksonTester.parseObject(json);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("name");
    }
}