package ru.andreyszdlv.authservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.authservice.enums.ERole;

import java.time.LocalDateTime;


@Data
@Entity(name = "t_pending_users")
@RequiredArgsConstructor
public class PendingUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String password;

    private ERole role;

    private LocalDateTime createdAt;
}
