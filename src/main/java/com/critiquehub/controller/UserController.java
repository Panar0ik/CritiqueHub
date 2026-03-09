package com.critiquehub.controller;

import com.critiquehub.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.critiquehub.model.User;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    // Используем constructor injection
    public UserController(final UserRepository userRepositoryParam) {
        this.userRepository = userRepositoryParam;
    }

    // GET: Получить всех пользователей
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // POST: Создать нового пользователя
    @PostMapping
    public User createUser(final @RequestBody User user) {
        return userRepository.save(user);
    }
}
