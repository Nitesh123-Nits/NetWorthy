package com.ashok.linkedInProject.postsService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostCreateRequestDto {
    @NotBlank(message = "Content cannot be blank")
    @Size(max = 3000, message = "Content cannot exceed 3000 characters")
    private String content;
}
