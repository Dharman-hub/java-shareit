package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void toUserDto_shouldMapUserToDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Oleg");
        user.setEmail("oleg@mail.ru");

        UserDto dto = UserMapper.toUserDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Oleg");
        assertThat(dto.getEmail()).isEqualTo("oleg@mail.ru");
    }

    @Test
    void toUser_shouldMapDtoToUser() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Oleg");
        dto.setEmail("oleg@mail.ru");

        User user = UserMapper.toUser(dto);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Oleg");
        assertThat(user.getEmail()).isEqualTo("oleg@mail.ru");
    }
}