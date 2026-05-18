package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void create_shouldCallClient() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Аккумуляторная дрель");
        itemDto.setAvailable(true);

        Mockito.when(itemClient.create(Mockito.eq(1L), Mockito.any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        Mockito.verify(itemClient).create(Mockito.eq(1L), Mockito.any(ItemDto.class));
    }

    @Test
    void create_whenNameIsBlank_thenBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("Описание");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(itemClient);
    }

    @Test
    void update_shouldCallClient() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Обновлённая дрель");

        Mockito.when(itemClient.update(Mockito.eq(1L), Mockito.eq(10L), Mockito.any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/10")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        Mockito.verify(itemClient).update(Mockito.eq(1L), Mockito.eq(10L), Mockito.any(ItemDto.class));
    }

    @Test
    void getById_shouldCallClient() throws Exception {
        Mockito.when(itemClient.getById(1L, 10L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/10")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        Mockito.verify(itemClient).getById(1L, 10L);
    }

    @Test
    void getByOwnerId_shouldCallClient() throws Exception {
        Mockito.when(itemClient.getByOwnerId(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        Mockito.verify(itemClient).getByOwnerId(1L);
    }

    @Test
    void search_shouldCallClient() throws Exception {
        Mockito.when(itemClient.search(1L, "дрель"))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, 1L)
                        .param("text", "дрель"))
                .andExpect(status().isOk());

        Mockito.verify(itemClient).search(1L, "дрель");
    }

    @Test
    void addComment_shouldCallClient() throws Exception {
        NewCommentDto commentDto = new NewCommentDto();
        commentDto.setText("Отличная вещь");

        Mockito.when(itemClient.addComment(Mockito.eq(1L), Mockito.eq(10L), Mockito.any(NewCommentDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/10/comment")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());

        Mockito.verify(itemClient).addComment(Mockito.eq(1L), Mockito.eq(10L), Mockito.any(NewCommentDto.class));
    }
}