package com.ashok.linkedInProject.postsService.service;

import com.ashok.linkedInProject.postsService.config.KafkaConfig;
import com.ashok.linkedInProject.postsService.entity.Post;
import com.ashok.linkedInProject.postsService.entity.PostLike;
import com.ashok.linkedInProject.postsService.event.PostLikedEvent;
import com.ashok.linkedInProject.postsService.repository.PostLikeRepository;
import com.ashok.linkedInProject.postsService.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostsRepository postsRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void likePost(Long postId, Long userId) {
        Post post = postsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new RuntimeException("You have already liked this post");
        }

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLikeRepository.save(postLike);

        // Publish Kafka event
        PostLikedEvent event = new PostLikedEvent(
                postId,
                userId,
                post.getUserId(),
                LocalDateTime.now());
        kafkaTemplate.send(KafkaConfig.POST_LIKED_TOPIC, postId.toString(), event);
        log.info("User {} liked post {}", userId, postId);
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        PostLike postLike = postLikeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("You have not liked this post"));
        postLikeRepository.delete(postLike);
        log.info("User {} unliked post {}", userId, postId);
    }

    public Long getLikesCount(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    public boolean isPostLikedByUser(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }
}
