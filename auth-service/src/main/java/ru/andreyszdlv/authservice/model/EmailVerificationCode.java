package ru.andreyszdlv.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "t_email_code")
public class EmailVerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_email", nullable = false)
    private String email;

    @Column(name = "c_verification_code", nullable = false)
    private String verificationCode;

    @Column(name = "c_expiration_time", nullable = false)
    private LocalDateTime expirationTime;
}
