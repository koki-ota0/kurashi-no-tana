package com.kurashi.repository;

import com.kurashi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByOpenId(String openId);

    Optional<User> findByEmail(String email);

    boolean existsByOpenId(String openId);
}
