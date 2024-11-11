package ru.practicum.api.publicAPI.compilation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.publicAPI.compilation.service.PublicCompilationService;
import ru.practicum.model.compilation.dto.CompilationResponseDto;
import ru.practicum.utils.Constants;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCompilationController {
    private final PublicCompilationService publicCompilationService;

    @GetMapping
    public List<CompilationResponseDto> getCompilation(@RequestParam(required = false) Boolean pinned,
                                                       @PositiveOrZero @RequestParam(defaultValue = Constants.ZERO) Integer from,
                                                       @Positive @RequestParam(defaultValue = Constants.TEN) Integer size) {
        log.info("Fetching compilations with params: pinned={}, from={}, size={}", pinned, from, size);
        return publicCompilationService.getCompilation(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationResponseDto getCompilationById(@PathVariable @NonNull Long compId) {
        log.info("Fetching compilation by id: {}", compId);
        return publicCompilationService.getCompilationById(compId);
    }
}
