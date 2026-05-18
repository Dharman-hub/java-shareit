package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public Object getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public Object getById(@PathVariable Long userId) {
        return userClient.getById(userId);
    }

    @PostMapping
    public Object create(@RequestBody UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }

        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public Object update(@PathVariable Long userId,
                         @RequestBody UserDto userDto) {
        if (userDto.getEmail() != null &&
                (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@"))) {
            throw new ValidationException("Некорректный email");
        }

        return userClient.update(userId, userDto);
    }
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userClient.delete(userId);
    }
}