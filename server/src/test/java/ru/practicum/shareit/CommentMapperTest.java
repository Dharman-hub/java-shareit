package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void toComment_shouldMapDtoToEntity() {
        NewCommentDto dto = new NewCommentDto();
        dto.setText("Отличная вещь");

        Comment comment = CommentMapper.toComment(dto, 1L, 2L);

        assertThat(comment.getText()).isEqualTo("Отличная вещь");
        assertThat(comment.getItemId()).isEqualTo(1L);
        assertThat(comment.getAuthorId()).isEqualTo(2L);
        assertThat(comment.getCreated()).isNotNull();
    }

    @Test
    void toCommentDto_shouldMapEntityToDto() {
        User author = new User();
        author.setName("Oleg");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Отличная вещь");

        CommentDto dto = CommentMapper.toCommentDto(comment, author);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Отличная вещь");
        assertThat(dto.getAuthorName()).isEqualTo("Oleg");
    }
}