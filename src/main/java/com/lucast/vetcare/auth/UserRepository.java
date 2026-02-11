package com.lucast.vetcare.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lucast.vetcare.common.enums.Role;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByActive(boolean active);
    long countByRole(Role role);
}
