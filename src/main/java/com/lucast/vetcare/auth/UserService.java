package com.lucast.vetcare.auth;

import com.lucast.vetcare.auth.dto.CreateUserRequest;
import com.lucast.vetcare.auth.dto.UpdateMeRequest;
import com.lucast.vetcare.auth.dto.UpdateUserRequest;
import com.lucast.vetcare.auth.dto.UserResponse;
import com.lucast.vetcare.common.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest req) {
        var currentUser = AuthContext.requireUser();

        if (currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can create users");
        }

        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        var user = new UserEntity();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(req.role());
        user.setActive(true);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        var saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole(),
                saved.isActive()
        );
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        requireAdmin();
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        requireAdmin();
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse me() {
        var current = AuthContext.requireUser();
        return toResponse(current);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest req) {
        requireAdmin();

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // email único
        if (req.email() != null && !req.email().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(req.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
            user.setEmail(req.email());
        }

        if (req.name() != null) user.setName(req.name());
        if (req.role() != null) user.setRole(req.role());
        if (req.active() != null) user.setActive(req.active());

        if (req.password() != null && !req.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(req.password()));
        }

        user.setUpdatedAt(OffsetDateTime.now());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateMe(UpdateMeRequest req) {
        var current = AuthContext.requireUser();

        // email único
        if (req.email() != null && !req.email().equalsIgnoreCase(current.getEmail())) {
            if (userRepository.existsByEmail(req.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
            current.setEmail(req.email());
        }

        if (req.name() != null) current.setName(req.name());

        if (req.password() != null && !req.password().isBlank()) {
            current.setPasswordHash(passwordEncoder.encode(req.password()));
        }

        current.setUpdatedAt(OffsetDateTime.now());
        return toResponse(userRepository.save(current));
    }

    @Transactional
    public void deleteLogical(Long id) {
        requireAdmin();

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // bloquear auto-delete do admin logado
        var current = AuthContext.requireUser();
        if (current.getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot deactivate your own user");
        }

        user.setActive(false);
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
    }

    private void requireAdmin() {
        var u = AuthContext.requireUser();
        if (u.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can perform this action");
        }
    }

    private UserResponse toResponse(UserEntity u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.isActive());
    }
}
