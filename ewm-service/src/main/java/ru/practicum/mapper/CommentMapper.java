package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.dto.CommentResponseDto;

@Mapper(componentModel = "spring", uses = {UserMapper.class, EventMapper.class})
public interface CommentMapper {

    @Mapping(target = "initiator", source = "comment.user")
    @Mapping(target = "event", source = "comment.event")
    @Mapping(target = "updatedAt", source = "comment.updatedAt")
    @Mapping(target = "approved", source = "comment.approved")
    CommentResponseDto toCommentResponseDto(Comment comment);
}
