package ru.practicum.model.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateDto {
    @Size(max = 255, message = "Comment text cannot exceed 255 characters")
    @NotBlank(message = "Comment text cannot be blank")
    private String text;
}
