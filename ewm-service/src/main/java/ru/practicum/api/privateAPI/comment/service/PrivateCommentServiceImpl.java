package ru.practicum.api.privateAPI.comment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.validation.ConflictException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.dto.CommentCreateDto;
import ru.practicum.model.comment.dto.CommentResponseDto;
import ru.practicum.model.comment.dto.CommentUpdateDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RepositoryHelper;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentServiceImpl implements PrivateCommentService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ObjectMapper objectMapper;
    private final RepositoryHelper repositoryHelper;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto createComment(Long userId, Long eventId, CommentCreateDto commentCreateDto) {
        User user = repositoryHelper.getUserIfExist(userId, userRepository);
        Event event = repositoryHelper.getEventIfExist(eventId, eventRepository);

        Comment comment = Comment.builder()
                .user(user)
                .event(event)
                .text(commentCreateDto.getText())
                .approved(false)
                .build();

        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponseDto(comment);
    }

    @Override
    public CommentResponseDto updateComment(Long userId, Long eventId, Long commentId, CommentUpdateDto commentUpdateDto) {
        User user = repositoryHelper.getUserIfExist(userId, userRepository);
        Event event = repositoryHelper.getEventIfExist(eventId, eventRepository);
        Comment comment = repositoryHelper.getCommentIfExist(commentId, commentRepository);

        if (!event.getInitiator().equals(user)) throw new ConflictException("User is not the creator of the comment");

        comment.setText(commentUpdateDto.getText());
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponseDto(comment);
    }

    @Override
    public CommentResponseDto approveComment(Long userId, Long eventId, Long commentId) {
        Comment comment = repositoryHelper.getCommentIfExist(commentId, commentRepository);
        Event event = repositoryHelper.getEventIfExist(eventId, eventRepository);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("You do not have permission to approve comments for this event");
        }

        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ConflictException("Comment does not belong to this event");
        }

        comment.setApproved(true);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponseDto(comment);
    }

    @Override
    public void delCommentById(Long userId, Long eventId, Long commentId) {
        User user = repositoryHelper.getUserIfExist(userId, userRepository);
        Comment comment = repositoryHelper.getCommentIfExist(commentId, commentRepository);
        repositoryHelper.getEventIfExist(eventId, eventRepository);


        if (!comment.getUser().equals(user)) throw new ConflictException("User is not the creator of the comment");
        commentRepository.delete(comment);
    }

    @Override
    public void delAllCommentsByEventId(Long userId, Long eventId) {
        User user = repositoryHelper.getUserIfExist(userId, userRepository);
        Event event = repositoryHelper.getEventIfExist(eventId, eventRepository);

        if (!event.getInitiator().equals(user)) throw new ConflictException("User is not the creator of the event");

        commentRepository.deleteAll();
    }

    @Override
    public List<CommentResponseDto> getAllCommentsByEventId(Long userId, Long eventId) {
        Event event = repositoryHelper.getEventIfExist(eventId, eventRepository);
        User user = repositoryHelper.getUserIfExist(userId, userRepository);

        if (user == null) {
            throw new ConflictException("You do not have permission to view comments for this event");
        }

        List<Comment> comments = commentRepository.findAllByEventIdAndApprovedTrue(eventId);

        return comments.stream()
                .map(commentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }
}
