package com.ashok.linkedInProject.postsService.repository;

import com.ashok.linkedInProject.postsService.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    Long countByPostId(Long postId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
