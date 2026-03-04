package com.kurashi.service;

import com.kurashi.entity.User;
import com.kurashi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByOpenId(String openId) {
        return userRepository.findByOpenId(openId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User register(String name, String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .openId("local-" + UUID.randomUUID())
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .loginMethod("local")
                .role(User.Role.USER)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User upsertUser(User user) {
        Optional<User> existingUser = userRepository.findByOpenId(user.getOpenId());

        if (existingUser.isPresent()) {
            User existing = existingUser.get();
            if (user.getName() != null) {
                existing.setName(user.getName());
            }
            if (user.getEmail() != null) {
                existing.setEmail(user.getEmail());
            }
            if (user.getLoginMethod() != null) {
                existing.setLoginMethod(user.getLoginMethod());
            }
            existing.setLastSignedIn(LocalDateTime.now());
            return userRepository.save(existing);
        }
        return userRepository.save(user);
    }
}
