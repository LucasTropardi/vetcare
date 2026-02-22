package com.lucast.vetcare.auth;

import com.lucast.vetcare.auth.dto.CreateUserRequest;
import com.lucast.vetcare.auth.dto.UpdateMeRequest;
import com.lucast.vetcare.auth.dto.UpdateUserRequest;
import com.lucast.vetcare.auth.dto.UserResponse;
import com.lucast.vetcare.auth.dto.UserStatsResponse;
import com.lucast.vetcare.auth.keycloak.KeycloakAdminService;
import com.lucast.vetcare.common.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
public class UserService {

    private static final int MAX_SIGNATURE_BASE64_CHARS = 3_000_000;

    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;

    public UserService(UserRepository userRepository,
                       KeycloakAdminService keycloakAdminService) {
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest req) {
        requireAdmin();

        if (userRepository.findByEmailIgnoreCase(req.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        validatePasswordConfirmation(req.password(), req.confirmPassword());

        keycloakAdminService.ensureUser(
                req.email().trim(),
                req.name().trim(),
                true,
                req.password(),
                req.role()
        );

        var user = new UserEntity();
        user.setName(req.name().trim());
        user.setEmail(req.email().trim());
        user.setPasswordHash("OIDC_EXTERNAL_ACCOUNT");
        user.setRole(req.role());
        user.setActive(true);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        applyProfessionalData(user, req.professionalLicense(), req.signatureImageBase64(), req.signatureImageContentType(), req.role());

        var saved = userRepository.save(user);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        requireAdmin();
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public UserStatsResponse stats() {
        requireAdmin();
        var total = userRepository.count();
        var active = userRepository.countByActive(true);
        var inactive = userRepository.countByActive(false);
        var admin = userRepository.countByRole(Role.ADMIN);
        var vet = userRepository.countByRole(Role.VET);
        var reception = userRepository.countByRole(Role.RECEPTION);

        return new UserStatsResponse(total, active, inactive, admin, vet, reception);
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

        String nextName = req.name() != null ? req.name().trim() : user.getName();
        String nextEmail = req.email() != null ? req.email().trim() : user.getEmail();
        Role nextRole = req.role() != null ? req.role() : user.getRole();
        boolean nextActive = req.active() != null ? req.active() : user.isActive();

        if (req.email() != null && !req.email().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(req.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
        }

        if (req.password() != null && !req.password().isBlank()) {
            validatePasswordConfirmation(req.password(), req.confirmPassword());
        }

        keycloakAdminService.ensureUser(
                nextEmail,
                nextName,
                nextActive,
                req.password(),
                nextRole
        );

        user.setName(nextName);
        user.setEmail(nextEmail);
        user.setRole(nextRole);
        user.setActive(nextActive);

        applyProfessionalData(user, req.professionalLicense(), req.signatureImageBase64(), req.signatureImageContentType(), nextRole);

        user.setUpdatedAt(OffsetDateTime.now());

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateMe(UpdateMeRequest req) {
        var current = AuthContext.requireUser();

        if (req.email() != null && !req.email().equalsIgnoreCase(current.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(req.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
            current.setEmail(req.email().trim());
        }

        if (req.name() != null) current.setName(req.name().trim());

        applyProfessionalData(current, req.professionalLicense(), req.signatureImageBase64(), req.signatureImageContentType(), current.getRole());

        keycloakAdminService.ensureUser(
                current.getEmail(),
                current.getName(),
                current.isActive(),
                null,
                current.getRole()
        );

        current.setUpdatedAt(OffsetDateTime.now());
        return toResponse(userRepository.save(current));
    }

    @Transactional
    public void deleteLogical(Long id) {
        requireAdmin();

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var current = AuthContext.requireUser();
        if (current.getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot deactivate your own user");
        }

        keycloakAdminService.ensureUser(
                user.getEmail(),
                user.getName(),
                false,
                null,
                user.getRole()
        );

        user.setActive(false);
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void deleteOldSchool(Long id) {
        requireAdmin();

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var current = AuthContext.requireUser();
        if (current.getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot deactivate your own user");
        }

        keycloakAdminService.ensureUser(
                user.getEmail(),
                user.getName(),
                false,
                null,
                user.getRole()
        );

        userRepository.deleteById(id);
    }

    private void requireAdmin() {
        Role role = AuthContext.requireRole();
        if (role != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can perform this action");
        }
    }

    private void validatePasswordConfirmation(String password, String confirmPassword) {
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }

        if (confirmPassword == null || confirmPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password confirmation is required");
        }

        if (!password.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password confirmation does not match");
        }
    }

    private void applyProfessionalData(UserEntity user,
                                       String professionalLicense,
                                       String signatureImageBase64,
                                       String signatureImageContentType,
                                       Role role) {
        if (role != Role.VET) {
            user.setProfessionalLicense(null);
            user.setSignatureImageBase64(null);
            user.setSignatureImageContentType(null);
            user.setSignatureUpdatedAt(null);
            return;
        }

        if (professionalLicense != null) {
            user.setProfessionalLicense(blankToNull(professionalLicense));
        }

        if (signatureImageBase64 != null) {
            String normalized = normalizeBase64(signatureImageBase64);
            user.setSignatureImageBase64(normalized);
            user.setSignatureUpdatedAt(normalized == null ? null : OffsetDateTime.now());
        }

        if (signatureImageContentType != null) {
            user.setSignatureImageContentType(blankToNull(signatureImageContentType));
        }

        if (user.getSignatureImageBase64() != null && user.getSignatureImageContentType() == null) {
            user.setSignatureImageContentType("image/png");
        }
    }

    private String normalizeBase64(String value) {
        String normalized = blankToNull(value);
        if (normalized == null) return null;

        if (normalized.length() > MAX_SIGNATURE_BASE64_CHARS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Signature image is too large");
        }

        if (normalized.startsWith("data:")) {
            int comma = normalized.indexOf(',');
            normalized = comma > 0 ? normalized.substring(comma + 1) : normalized;
        }

        return normalized.replaceAll("\\s+", "");
    }

    private String blankToNull(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

    private UserResponse toResponse(UserEntity u) {
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getRole(),
                u.isActive(),
                u.getProfessionalLicense(),
                u.getSignatureImageBase64(),
                u.getSignatureImageContentType()
        );
    }
}
