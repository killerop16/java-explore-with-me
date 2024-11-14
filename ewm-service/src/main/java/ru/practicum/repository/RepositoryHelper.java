package ru.practicum.repository;

import org.springframework.stereotype.Component;
import ru.practicum.exception.validation.ResourceNotFoundException;
import ru.practicum.model.category.Category;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.event.Event;
import ru.practicum.model.location.Location;
import ru.practicum.model.participationRequest.ParticipationRequest;
import ru.practicum.model.user.User;
import ru.practicum.utils.Constants;

@Component
public class RepositoryHelper {
    public User getUserIfExist(Long userId, UserRepository userRepository) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Constants.USER_NOT_FOUND, userId)));
    }

    public Category getCategoryIfExist(Long categoryId, CategoryRepository categoryRepository) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(
                String.format(Constants.CATEGORY_NOT_FOUND, categoryId)));
    }

    public Event getEventIfExist(Long eventId, EventRepository eventRepository) {
        return eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException(
                String.format(Constants.EVENT_NOT_FOUND, eventId)));
    }

    public Compilation getCompilationIfExist(Long compilationId, CompilationRepository compilationRepository) {
        return compilationRepository.findById(compilationId).orElseThrow(() -> new ResourceNotFoundException(
                String.format(Constants.COMPILATION_NOT_FOUND, compilationId)));
    }

    public ParticipationRequest getParticipationRequestIfExist(Long id, ParticipationRequestRepository participationRequestRepository) {
        return participationRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                String.format(Constants.PARTICIPATION_REQUEST_NOT_FOUND, id)));
    }

    public Location getLocationIfExist(Long id, LocationRepository locationRepository) {
        return locationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                String.format(Constants.LOCATION_NOT_FOUND, id)));
    }

    public Comment getCommentIfExist(Long id, CommentRepository commentRepository) {
        return commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                String.format(Constants.COMMENT_NOT_FOUND, id)));
    }

    public boolean isCategoryUsed(Long categoryId, EventRepository eventRepository) {
        return eventRepository.existsByEventCategoryId(categoryId);
    }


}
