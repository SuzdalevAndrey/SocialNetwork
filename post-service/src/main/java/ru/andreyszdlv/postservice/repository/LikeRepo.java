package ru.andreyszdlv.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andreyszdlv.postservice.model.Like;

@Repository
public interface LikeRepo extends JpaRepository<Like, Long> {
    boolean deleteByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
