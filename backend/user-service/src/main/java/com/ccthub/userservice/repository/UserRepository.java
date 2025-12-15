package com.ccthub.userservice.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ccthub.userservice.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);

    Long countByLastLoginTimeAfter(LocalDateTime time);

    Page<User> findByPhoneContaining(String phone, Pageable pageable);

    Page<User> findByStatus(String status, Pageable pageable);

    Page<User> findByPhoneContainingAndStatus(String phone, String status, Pageable pageable);
}
