package ru.practicum.api.publicAPI.compilation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.repository.RepositoryHelper;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.repository.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final ObjectMapper mapper;
    private final RepositoryHelper validation;

    @Override
    public CompilationResponseDto getCompilationById(Long compId) {
        log.info("Fetching compilation by id: {}", compId);
        Compilation compilation  = validation.getCompilationIfExist(compId, compilationRepository);
        return mapper.convertValue(compilation, CompilationResponseDto.class);
    }

    @Override
    public List<CompilationResponseDto> getCompilation(Boolean pinned, Integer from, Integer size) {
        log.info("Fetching compilations with params: pinned={}, from={}, size={}", pinned, from, size);
        Pageable pageable = PageRequest.of(from / size, size);

        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageable).getContent();
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }

        return compilations.stream()
                .map(compilation -> mapper.convertValue(compilation, CompilationResponseDto.class))
                .collect(Collectors.toList());
    }
}
