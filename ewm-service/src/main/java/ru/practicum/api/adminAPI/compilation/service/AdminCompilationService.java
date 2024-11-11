package ru.practicum.api.adminAPI.compilation.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.model.compilation.dto.CreateCompilationDto;
import ru.practicum.model.compilation.dto.UpdateCompilationDto;

public interface AdminCompilationService {
    CompilationResponseDto createCompilation(CreateCompilationDto newCompilationDto);

    void delCompilation(Long compId);

    CompilationResponseDto updateCompilation(Long compId, UpdateCompilationDto compilationRequest) throws JsonMappingException;
}
