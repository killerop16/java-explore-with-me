package ru.practicum.model.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.event.dto.EventShortResponseDto;
import ru.practicum.model.user.dto.UserShortDto;
import ru.practicum.utils.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Boolean approved = false;
    private String text;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime updatedAt;
    private UserShortDto initiator;
    private EventShortResponseDto event;
}
