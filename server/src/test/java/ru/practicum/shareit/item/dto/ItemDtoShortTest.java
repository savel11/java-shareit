package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoShortTest {
    @Autowired
    private JacksonTester<ItemDtoShort> jacksonTester;

    @Test
    public void testSerialize() throws Exception {
        final ItemDtoShort itemDto = new ItemDtoShort(1L, "item", 1L);
        JsonContent<ItemDtoShort> result = jacksonTester.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"name\"}";
        ItemDtoShort dto = jacksonTester.parseObject(json);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("name");
    }
}

