package ru.practicum.api.adminAPI.category;

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
import ru.practicum.api.adminAPI.category.service.AdminCategoryService;
import ru.practicum.model.category.dto.CategoryCreateDto;
import ru.practicum.model.category.dto.CategoryResponseDto;
import ru.practicum.model.category.dto.CategoryUpdateDto;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(@RequestBody @Valid CategoryCreateDto newCategoryDto) {
        log.info("Received request to create category with name: {}", newCategoryDto.getName());
        CategoryResponseDto categoryResponse = adminCategoryService.createCategory(newCategoryDto);
        log.info("Category created successfully: {}", categoryResponse.getName());
        return categoryResponse;
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delCategoryById(@PathVariable @Positive Long categoryId) {
        log.info("Received request to delete category with id: {}", categoryId);
        adminCategoryService.delCategory(categoryId);
        log.info("Category with id: {} deleted successfully", categoryId);
    }

    @PatchMapping("/{categoryId}")
    public CategoryResponseDto updateCategoryById(@PathVariable @Positive Long categoryId,
                                              @RequestBody @Valid CategoryUpdateDto categoryUpdateDto) {
        log.info("Received request to update category with id: {}, new name: {}", categoryId, categoryUpdateDto.getName());
        CategoryResponseDto updatedCategory = adminCategoryService.updateCategory(categoryId, categoryUpdateDto);
        log.info("Category with id: {} updated successfully to name: {}", categoryId, updatedCategory.getName());
        return updatedCategory;
    }
}

