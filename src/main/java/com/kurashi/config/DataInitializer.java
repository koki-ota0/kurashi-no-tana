package com.kurashi.config;

import com.kurashi.repository.UserRepository;
import com.kurashi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("demo@example.com")) {
            userService.register("デモユーザー", "demo@example.com", "password123");
        }
    }
}
