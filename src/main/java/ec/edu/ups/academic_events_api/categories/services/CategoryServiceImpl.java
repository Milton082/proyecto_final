package ec.edu.ups.academic_events_api.categories.services;

import ec.edu.ups.academic_events_api.categories.dtos.CategoryResponseDto;
import ec.edu.ups.academic_events_api.categories.dtos.CreateCategoryDto;
import ec.edu.ups.academic_events_api.categories.dtos.UpdateCategoryDto;
import ec.edu.ups.academic_events_api.categories.entities.CategoryEntity;
import ec.edu.ups.academic_events_api.categories.mappers.CategoryMapper;
import ec.edu.ups.academic_events_api.categories.repositories.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> findAll() {
        return categoryRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(CategoryMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto findOne(Long id) {
        CategoryEntity category = findActiveCategory(id);

        return CategoryMapper.toResponseDto(category);
    }

    @Override
    public CategoryResponseDto create(CreateCategoryDto dto) {
        String normalizedName = dto.getName().trim();

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una categoría con el nombre indicado");
        }

        CategoryEntity category = CategoryMapper.toEntity(dto);
        CategoryEntity savedCategory = categoryRepository.save(category);

        return CategoryMapper.toResponseDto(savedCategory);
    }

    @Override
    public CategoryResponseDto update(Long id, UpdateCategoryDto dto) {
        CategoryEntity category = findActiveCategory(id);
        String normalizedName = dto.getName().trim();

        boolean duplicatedName =
                categoryRepository.existsByNameIgnoreCaseAndIdNot(
                        normalizedName,id);

        if (duplicatedName) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe otra categoría con el nombre indicado");
        }

        CategoryMapper.updateEntity(category, dto);
        CategoryEntity updatedCategory = categoryRepository.save(category);

        return CategoryMapper.toResponseDto(updatedCategory);
    }

    @Override
    public void delete(Long id) {
        CategoryEntity category = findActiveCategory(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryEntity findActiveCategory(Long id) {
        return categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró una categoría activa con el ID " + id));
    }
}