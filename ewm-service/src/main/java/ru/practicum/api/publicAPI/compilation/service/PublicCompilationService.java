package ru.practicum.api.publicAPI.compilation.service;

import ru.practicum.model.compilation.dto.CompilationResponseDto;

import java.util.List;

public interface PublicCompilationService {
    CompilationResponseDto getCompilationById(Long compId);

    List<CompilationResponseDto> getCompilation(Boolean pinned, Integer from, Integer size);
}
