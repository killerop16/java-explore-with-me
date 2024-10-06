package ru.practicum.api.adminAPI.compilation.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.validation.Validation;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.model.compilation.dto.CreateCompilationDto;
import ru.practicum.model.compilation.dto.UpdateCompilationDto;
import ru.practicum.model.event.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final Validation validation;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationResponseDto createCompilation(CreateCompilationDto newCompilationDto) {
        log.info("Creating a new compilation with title: {}", newCompilationDto.getTitle());
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
            compilation.setEvents(events);
        }

        compilationRepository.save(compilation);
        log.info("Compilation created successfully with id: {}", compilation.getId());
        return compilationMapper.toResponseDto(compilation);
    }

    @Override
    public void delCompilation(Long compId) {
        log.info("Deleting compilation with id: {}", compId);
        validation.checkCompilationExist(compId, compilationRepository);
        compilationRepository.deleteById(compId);
        log.info("Compilation with id: {} deleted successfully", compId);
    }

    @Override
    public CompilationResponseDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) throws JsonMappingException {
        log.info("Updating compilation with id: {}. New details: {}", compId, updateCompilationDto);
        Compilation compilation = validation.checkCompilationExist(compId, compilationRepository);

        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }

        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }

        // Если в запросе указаны события, обновляем список событий в компиляции
        if (updateCompilationDto.getEvents() != null && !updateCompilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(updateCompilationDto.getEvents());
            compilation.setEvents(events);
            log.info("Updated events in the compilation with id: {}", compId);
        }

        // Сохраняем обновлённую компиляцию
        Compilation updatedCompilation = compilationRepository.save(compilation);
        log.info("Compilation with id: {} updated successfully", updatedCompilation.getId());

        // Преобразуем обратно в DTO для возврата
        return compilationMapper.toResponseDto(updatedCompilation);
    }

}
