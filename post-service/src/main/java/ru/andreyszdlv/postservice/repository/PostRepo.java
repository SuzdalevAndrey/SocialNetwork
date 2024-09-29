package ru.andreyszdlv.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andreyszdlv.postservice.model.Post;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepo extends JpaRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);
}
