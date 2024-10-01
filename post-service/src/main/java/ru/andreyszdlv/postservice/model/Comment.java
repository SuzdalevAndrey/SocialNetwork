package ru.andreyszdlv.postservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_comments")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_content", nullable = false)
    private String content;

    @Column(name = "c_date_create", nullable = false)
    private LocalDateTime dateCreate;

    @Column(name = "c_user_id", nullable = false)
    Long userId;

    @Column(name = "c_post_id", nullable = false)
    Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_post_id", insertable = false, updatable = false)
    @JsonIgnore
    private Post post;
}
