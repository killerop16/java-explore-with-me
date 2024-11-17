package ru.practicum.api.privateAPI.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.privateAPI.comment.service.PrivateCommentService;
import ru.practicum.model.comment.dto.CommentCreateDto;
import ru.practicum.model.comment.dto.CommentResponseDto;
import ru.practicum.model.comment.dto.CommentUpdateDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@Validated
@Slf4j
public class PrivateCommentController {

    private final PrivateCommentService privateCommentService;

    @PostMapping
    public CommentResponseDto createComment(@PathVariable @Positive Long userId,
                                            @PathVariable @Positive Long eventId,
                                            @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Creating comment for user {} on event {}", userId, eventId);
        return privateCommentService.createComment(userId, eventId, commentCreateDto);
    }

    @PatchMapping("/{commentId}")
    public CommentResponseDto updateComment(@PathVariable @Positive Long userId,
                                            @PathVariable @Positive Long eventId,
                                            @PathVariable @Positive Long commentId,
                                            @RequestBody @Valid CommentUpdateDto commentUpdateDto) {
        log.info("Updating comment {} for user {} on event {}", commentId, userId, eventId);
        return privateCommentService.updateComment(userId, eventId, commentId, commentUpdateDto);
    }

    @PatchMapping("/{commentId}/approve")
    public CommentResponseDto approveComment(@PathVariable @Positive Long userId,
                                             @PathVariable @Positive Long eventId,
                                             @PathVariable @Positive Long commentId) {
        log.info("Approve comment {} for user {} on event {}", commentId, userId, eventId);
        return privateCommentService.approveComment(userId, eventId, commentId);
    }


    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delCommentById(@PathVariable @Positive Long userId,
                               @PathVariable @Positive Long eventId,
                               @PathVariable @Positive Long commentId) {
        log.info("Deleting comment {} for user {} on event {}", commentId, userId, eventId);
        privateCommentService.delCommentById(userId, eventId, commentId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delAllCommentsByEventId(@PathVariable @Positive Long userId,
                                        @PathVariable @Positive Long eventId) {
        log.info("Deleting all comments for user {} on event {}", userId, eventId);
        privateCommentService.delAllCommentsByEventId(userId, eventId);
    }

    @GetMapping
    public List<CommentResponseDto> getAllCommentsByEventId(@PathVariable @Positive Long userId,
                                                            @PathVariable @Positive Long eventId) {
        log.info("Fetching all comments for user {} on event {}", userId, eventId);
        return privateCommentService.getAllCommentsByEventId(userId, eventId);
    }
}
