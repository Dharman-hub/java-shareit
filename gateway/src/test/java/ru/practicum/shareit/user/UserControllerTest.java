package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void getAll_shouldCallClient() throws Exception {
        Mockito.when(userClient.getAll())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        Mockito.verify(userClient).getAll();
    }

    @Test
    void getById_shouldCallClient() throws Exception {
        Mockito.when(userClient.getById(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(userClient).getById(1L);
    }

    @Test
    void create_shouldCallClient() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Oleg");
        userDto.setEmail("oleg@mail.ru");

        Mockito.when(userClient.create(Mockito.any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        Mockito.verify(userClient).create(Mockito.any(UserDto.class));
    }

    @Test
    void create_whenEmailIsInvalid_thenBadRequest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Oleg");
        userDto.setEmail("bad-email");

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userClient);
    }

    @Test
    void update_shouldCallClient() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("New Oleg");
        userDto.setEmail("new@mail.ru");

        Mockito.when(userClient.update(Mockito.eq(1L), Mockito.any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        Mockito.verify(userClient).update(Mockito.eq(1L), Mockito.any(UserDto.class));
    }

    @Test
    void delete_shouldCallClient() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(userClient).delete(1L);
    }
}