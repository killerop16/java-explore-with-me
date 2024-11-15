package ru.practicum.api.publicAPI.category.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryResponseDto;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.RepositoryHelper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryServiceImpl implements PublicCategoryService {
    private final CategoryRepository categoryRepository;
    private final ObjectMapper mapper;
    private final RepositoryHelper validation;

    @Override
    public CategoryResponseDto getCategoriesById(Long catId) {
        log.info("Fetching category with ID: {}", catId);
        Category category = validation.getCategoryIfExist(catId, categoryRepository);
        log.info("Category found: {}", category);
        return mapper.convertValue(category, CategoryResponseDto.class);
    }

    @Override
    public List<CategoryResponseDto> getCategories(Integer from, Integer size) {
        log.info("Fetching categories with pagination - from: {}, size: {}", from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<CategoryResponseDto> categories = categoryRepository.findAll(pageable)
                .map(category -> mapper.convertValue(category, CategoryResponseDto.class))
                .getContent();
        log.info("Found {} categories", categories.size());
        return categories;
    }
}
