package com.ashok.linkedInProject.postsService.service;

import com.ashok.linkedInProject.postsService.config.KafkaConfig;
import com.ashok.linkedInProject.postsService.dto.PostCreateRequestDto;
import com.ashok.linkedInProject.postsService.dto.PostDto;
import com.ashok.linkedInProject.postsService.entity.Post;
import com.ashok.linkedInProject.postsService.event.PostCreatedEvent;
import com.ashok.linkedInProject.postsService.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostsRepository postsRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @CacheEvict(value = "userPosts", key = "#postCreateRequestDto.userId != null ? #postCreateRequestDto.userId : 'default'", condition = "#postCreateRequestDto.userId != null")
    public PostDto createPost(PostCreateRequestDto postCreateRequestDto, Long userId) {
        Post post = modelMapper.map(postCreateRequestDto, Post.class);
        post.setUserId(userId);
        Post savedPost = postsRepository.save(post);

        // Publish Kafka event
        PostCreatedEvent event = new PostCreatedEvent(
                savedPost.getId(),
                savedPost.getUserId(),
                savedPost.getContent(),
                savedPost.getCreatedAt());
        kafkaTemplate.send(KafkaConfig.POST_CREATED_TOPIC, savedPost.getId().toString(), event);
        log.info("Published post_created event for postId: {}", savedPost.getId());

        return modelMapper.map(savedPost, PostDto.class);
    }

    @Cacheable(value = "posts", key = "#postId")
    public PostDto getPostById(Long postId) {
        Post post = postsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        return modelMapper.map(post, PostDto.class);
    }

    @Cacheable(value = "userPosts", key = "#userId")
    public List<PostDto> getPostsByUserId(Long userId) {
        List<Post> posts = postsRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    public List<PostDto> getAllPosts() {
        List<Post> posts = postsRepository.findAllByOrderByCreatedAtDesc();
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    @CacheEvict(value = { "posts", "userPosts" }, allEntries = true)
    public void deletePost(Long postId, Long userId) {
        Post post = postsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this post");
        }
        postsRepository.delete(post);
        log.info("Deleted post with id: {}", postId);
    }
}
