package ru.practicum.api.publicAPI.category.service;

import ru.practicum.model.category.dto.CategoryResponseDto;

import java.util.List;

public interface PublicCategoryService {
    CategoryResponseDto getCategoriesById(Long catId);

    List<CategoryResponseDto> getCategories(Integer from, Integer size);
}
