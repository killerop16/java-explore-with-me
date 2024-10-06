package ru.practicum.api.publicAPI.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.publicAPI.category.service.PublicCategoryService;
import ru.practicum.model.category.dto.CategoryResponseDto;
import ru.practicum.utils.Constants;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCategoryController {
    private final PublicCategoryService publicCategoryService;

    @GetMapping
    public List<CategoryResponseDto> getCategories(@PositiveOrZero @RequestParam(defaultValue = Constants.ZERO) Integer from,
                                                   @Positive @RequestParam(defaultValue = Constants.TEN) Integer size) {
        log.info("Fetching categories, from = {}, size = {}", from, size);
        List<CategoryResponseDto> categories = publicCategoryService.getCategories(from, size);
        log.info("Found {} categories", categories.size());
        return categories;
    }

    @GetMapping("/{catId}")
    public CategoryResponseDto getCategoriesById(@PathVariable @NotNull Long catId) {

        log.info("Fetching category by ID: {}", catId);
        CategoryResponseDto category = publicCategoryService.getCategoriesById(catId);
        log.info("Found category: {}", category);
        return category;
    }
}
