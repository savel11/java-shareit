package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @Test
    public void testSerialize() throws Exception {
        final CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Comment");
        dto.setAuthorName("Me");
        JsonContent<CommentDto> json = jacksonTester.write(dto);
        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.authorName").isEqualTo("Me");
        assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo("Comment");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"text\":\"comment\",\"author\":\"me\"}";
        CommentDto dto = jacksonTester.parseObject(json);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getText()).isEqualTo("comment");
    }
}
