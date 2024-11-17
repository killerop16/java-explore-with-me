package ru.practicum.api.adminAPI.category.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.validation.ConflictException;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryCreateDto;
import ru.practicum.model.category.dto.CategoryResponseDto;
import ru.practicum.model.category.dto.CategoryUpdateDto;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RepositoryHelper;
import ru.practicum.utils.Constants;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final ObjectMapper mapper;
    private final RepositoryHelper validation;

    @Override
    public CategoryResponseDto createCategory(CategoryCreateDto newCategoryDto) {
        log.info("Creating new category with name: {}", newCategoryDto.getName());
        Category category = mapper.convertValue(newCategoryDto, Category.class);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", savedCategory.getId());
        return mapper.convertValue(savedCategory, CategoryResponseDto.class);
    }

    @Override
    public void delCategory(Long categoryId) {
        log.info("Attempting to delete category with id: {}", categoryId);

        // Проверка на использование категории
        if (validation.isCategoryUsed(categoryId, eventRepository)) {
            log.warn("Attempted to delete busy category with id = {}. Category cannot be deleted.", categoryId);
            throw new ConflictException(String.format(Constants.BUSY_CATEGORY, categoryId));
        }

        // Удаление категории
        categoryRepository.deleteById(categoryId);
        log.info("Category with id: {} deleted successfully", categoryId);
    }

    @Override
    public CategoryResponseDto updateCategory(Long categoryId, CategoryUpdateDto categoryUpdateDto) {
        log.info("Updating category with id: {}. New name: {}", categoryId, categoryUpdateDto.getName());
        Category category = validation.getCategoryIfExist(categoryId, categoryRepository);

        category.setName(categoryUpdateDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category with id: {} updated successfully to new name: {}", categoryId, updatedCategory.getName());
        return mapper.convertValue(updatedCategory, CategoryResponseDto.class);
    }
}
