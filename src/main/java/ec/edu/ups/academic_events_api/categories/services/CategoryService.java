package ec.edu.ups.academic_events_api.categories.services;

import ec.edu.ups.academic_events_api.categories.dtos.CategoryResponseDto;
import ec.edu.ups.academic_events_api.categories.dtos.CreateCategoryDto;
import ec.edu.ups.academic_events_api.categories.dtos.UpdateCategoryDto;
import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> findAll();
    CategoryResponseDto findOne(Long id);
    CategoryResponseDto create(CreateCategoryDto dto);
    CategoryResponseDto update(Long id, UpdateCategoryDto dto);
    void delete(Long id);
}