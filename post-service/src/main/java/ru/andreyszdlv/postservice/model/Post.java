package ru.andreyszdlv.postservice.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(schema = "posts", name="t_posts")
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_content", nullable = false)
    @Size(min = 1, max = 1000, message = "{error.post.content.is_not_valid}")
    private String content;

    @Column(name = "c_number_views", columnDefinition = "bigint default 0")
    private Long numberViews;

    @Column(name = "c_date_create", nullable = false)
    private LocalDateTime dateCreate;

//    @NotBlank(message = "{error.post.user_id.is_empty}")
    @Column(name = "c_user_id", nullable = false)
    private Long userId;
}
