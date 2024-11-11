package ru.practicum.model.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationDto {
    private List<Long> events;
//    private Long id;
    private Boolean pinned;
    @Size(max = 50)
    private String title;
}
