package com.lucast.vetcare.auth;

import com.lucast.vetcare.common.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 160, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 120)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "professional_license", length = 80)
    private String professionalLicense;

    @Column(name = "signature_image_base64", columnDefinition = "text")
    private String signatureImageBase64;

    @Column(name = "signature_image_content_type", length = 80)
    private String signatureImageContentType;

    @Column(name = "signature_updated_at")
    private OffsetDateTime signatureUpdatedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
