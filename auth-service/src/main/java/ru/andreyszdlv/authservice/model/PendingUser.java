package ru.andreyszdlv.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.authservice.enums.ERole;

import java.time.LocalDateTime;


@Data
@Entity(name = "t_pending_users")
public class PendingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_name", nullable = false)
    private String name;

    @Column(name = "c_email", nullable = false)
    private String email;

    @Column(name = "c_password", nullable = false)
    private String password;

    @Column(name = "c_role", nullable = false)
    private ERole role;

    @Column(name = "c_created_at", nullable = false)
    private LocalDateTime createdAt;
}
