package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:shareit-user-test;MODE=PostgreSQL;DATABASE_TO_UPPER=false",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always"
})
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void create_shouldSaveUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Oleg");
        userDto.setEmail("oleg-user@mail.ru");

        UserDto result = userService.create(userDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Oleg");
        assertThat(result.getEmail()).isEqualTo("oleg-user@mail.ru");
    }

    @Test
    void update_shouldUpdateUserFields() {
        UserDto userDto = new UserDto();
        userDto.setName("Oleg");
        userDto.setEmail("oleg-update@mail.ru");

        UserDto saved = userService.create(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("New Oleg");
        updateDto.setEmail("new-oleg@mail.ru");

        UserDto updated = userService.update(saved.getId(), updateDto);

        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getName()).isEqualTo("New Oleg");
        assertThat(updated.getEmail()).isEqualTo("new-oleg@mail.ru");
    }

    @Test
    void getAll_shouldReturnUsers() {
        UserDto userDto = new UserDto();
        userDto.setName("Oleg");
        userDto.setEmail("oleg-all@mail.ru");

        userService.create(userDto);

        assertThat(userService.getAll()).isNotEmpty();
    }
}