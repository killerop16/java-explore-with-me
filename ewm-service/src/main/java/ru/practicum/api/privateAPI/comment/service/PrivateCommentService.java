package ru.practicum.api.privateAPI.comment.service;

import ru.practicum.model.comment.dto.CommentCreateDto;
import ru.practicum.model.comment.dto.CommentResponseDto;
import ru.practicum.model.comment.dto.CommentUpdateDto;

import java.util.List;

public interface PrivateCommentService {
    CommentResponseDto createComment(Long userId, Long eventId, CommentCreateDto commentCreateDto);

    CommentResponseDto updateComment(Long userId, Long eventId, Long commentId, CommentUpdateDto commentUpdateDto);

    CommentResponseDto approveComment(Long userId, Long eventId, Long commentId);

    void delCommentById(Long userId, Long eventId, Long commentId);

    void delAllCommentsByEventId(Long userId, Long eventId);

    List<CommentResponseDto> getAllCommentsByEventId(Long userId, Long eventId);
}
