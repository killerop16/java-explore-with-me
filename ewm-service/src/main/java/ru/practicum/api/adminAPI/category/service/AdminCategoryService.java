package ru.practicum.api.adminAPI.category.service;

import ru.practicum.model.category.dto.CategoryCreateDto;
import ru.practicum.model.category.dto.CategoryResponseDto;
import ru.practicum.model.category.dto.CategoryUpdateDto;

public interface AdminCategoryService {
    CategoryResponseDto createCategory(CategoryCreateDto newCategoryDto);

    void delCategory(Long categoryId);

    CategoryResponseDto updateCategory(Long categoryId, CategoryUpdateDto categoryUpdateDto);
}
