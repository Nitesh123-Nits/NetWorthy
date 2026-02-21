package com.ashok.linkedInProject.postsService.controller;

import com.ashok.linkedInProject.postsService.service.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
@Tag(name = "Post Likes", description = "Post likes management APIs")
public class PostLikesController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}")
    @Operation(summary = "Like a post")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        postLikeService.likePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Unlike a post")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        postLikeService.unlikePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/count")
    @Operation(summary = "Get likes count for a post")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long postId) {
        return ResponseEntity.ok(postLikeService.getLikesCount(postId));
    }

    @GetMapping("/{postId}/is-liked")
    @Operation(summary = "Check if current user liked a post")
    public ResponseEntity<Boolean> isPostLikedByUser(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(postLikeService.isPostLikedByUser(postId, userId));
    }
}
