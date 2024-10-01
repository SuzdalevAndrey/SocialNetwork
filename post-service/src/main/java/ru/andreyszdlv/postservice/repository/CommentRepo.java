package ru.andreyszdlv.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andreyszdlv.postservice.model.Comment;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
}
