package e_learning.catalog_service.services;

import e_learning.catalog_service.dto.*;
import e_learning.catalog_service.entities.*;
import e_learning.catalog_service.exceptions.*;
import e_learning.catalog_service.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public CategoryService(CategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .build();

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        // Check for duplicate name (excluding current category)
        categoryRepository.findByName(request.getName())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new DuplicateResourceException(
                            "Category with name '" + request.getName() + "' already exists");
                });

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIconUrl(request.getIconUrl());

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }

        Long courseCount = courseRepository.countByCategoryId(id);
        if (courseCount > 0) {
            throw new DuplicateResourceException("Cannot delete category with " + courseCount + " associated courses");
        }

        categoryRepository.deleteById(id);
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return mapToResponse(category);
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CategoryResponse mapToResponse(Category category) {
        Long courseCount = courseRepository.countByCategoryId(category.getId());
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .iconUrl(category.getIconUrl())
                .courseCount(courseCount)
                .build();
    }
}
