package ec.edu.ups.academic_events_api.categories.mappers;

import ec.edu.ups.academic_events_api.categories.dtos.CategoryResponseDto;
import ec.edu.ups.academic_events_api.categories.dtos.CreateCategoryDto;
import ec.edu.ups.academic_events_api.categories.dtos.UpdateCategoryDto;
import ec.edu.ups.academic_events_api.categories.entities.CategoryEntity;

public class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryEntity toEntity(CreateCategoryDto dto) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(dto.getName().trim());
        entity.setDescription(normalizeDescription(dto.getDescription()));
        entity.setActive(true);

        return entity;
    }

    public static void updateEntity(CategoryEntity entity, UpdateCategoryDto dto) {
        entity.setName(dto.getName().trim());
        entity.setDescription(normalizeDescription(dto.getDescription()));
        entity.setActive(dto.getActive());
    }

    public static CategoryResponseDto toResponseDto(CategoryEntity entity) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    private static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}