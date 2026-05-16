package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto create(UserDto userDto) {
        validateUser(userDto);

        if (userStorage.emailExists(userDto.getEmail(), null)) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }

        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        checkUserExists(userId);

        if (userDto.getEmail() != null) {
            validateUser(userDto);

            if (userStorage.emailExists(userDto.getEmail(), userId)) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
        }

        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.update(userId, user));
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(userStorage.getById(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void delete(Long userId) {
        checkUserExists(userId);
        userStorage.delete(userId);
    }

    private void validateUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }
    }

    private void checkUserExists(Long userId) {
        userStorage.getById(userId);
    }
}