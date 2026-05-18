package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    @Test
    void create_shouldCallClient() throws Exception {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Нужна дрель");

        Mockito.when(requestClient.create(Mockito.eq(1L), Mockito.any(NewItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.verify(requestClient).create(Mockito.eq(1L), Mockito.any(NewItemRequestDto.class));
    }

    @Test
    void create_whenDescriptionIsBlank_thenBadRequest() throws Exception {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("");

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(requestClient);
    }

    @Test
    void getUserRequests_shouldCallClient() throws Exception {
        Mockito.when(requestClient.getUserRequests(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        Mockito.verify(requestClient).getUserRequests(1L);
    }

    @Test
    void getAllRequests_shouldCallClient() throws Exception {
        Mockito.when(requestClient.getAllRequests(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        Mockito.verify(requestClient).getAllRequests(1L);
    }

    @Test
    void getById_shouldCallClient() throws Exception {
        Mockito.when(requestClient.getById(1L, 10L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/10")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        Mockito.verify(requestClient).getById(1L, 10L);
    }
}