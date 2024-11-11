package ru.practicum.api.adminAPI.compilation;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.adminAPI.compilation.service.AdminCompilationService;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.model.compilation.dto.CreateCompilationDto;
import ru.practicum.model.compilation.dto.UpdateCompilationDto;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminCompilationController {

    private final AdminCompilationService adminCompilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto createCompilation(@RequestBody @Valid CreateCompilationDto newCompilationDto) {
        log.info("Creating a new compilation with title: {}", newCompilationDto.getTitle());
       return adminCompilationService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delCompilationById(@PathVariable @Positive Long compId) {
        log.info("Deleting compilation with id: {}", compId);
        adminCompilationService.delCompilation(compId);
        log.info("Compilation with id: {} deleted successfully", compId);
    }

    @PatchMapping("{compId}")
    public CompilationResponseDto updateCompilationById(@PathVariable @Positive Long compId,
                                                        @RequestBody @Valid UpdateCompilationDto compilationRequest) throws JsonMappingException {
        log.info("Updating compilation with id: {}. New details: {}", compId, compilationRequest);
        CompilationResponseDto response = adminCompilationService.updateCompilation(compId, compilationRequest);
        log.info("Compilation with id: {} updated successfully", compId);
        return response;
    }
}
