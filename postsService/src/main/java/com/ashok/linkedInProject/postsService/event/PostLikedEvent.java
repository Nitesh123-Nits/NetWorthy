package com.ashok.linkedInProject.postsService.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLikedEvent {
    private Long postId;
    private Long likedByUserId;
    private Long postOwnerId;
    private LocalDateTime likedAt;
}
