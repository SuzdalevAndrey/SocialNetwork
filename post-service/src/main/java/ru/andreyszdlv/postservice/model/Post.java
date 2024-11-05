package ru.andreyszdlv.postservice.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Table(name="t_posts")
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

    @Column(name = "c_user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Like> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @ElementCollection
    @CollectionTable(name = "t_post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_id")
    private List<String> imageIds;
}
