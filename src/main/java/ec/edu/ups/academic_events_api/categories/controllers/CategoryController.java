package ec.edu.ups.academic_events_api.categories.controllers;

import ec.edu.ups.academic_events_api.categories.dtos.CategoryResponseDto;
import ec.edu.ups.academic_events_api.categories.dtos.CreateCategoryDto;
import ec.edu.ups.academic_events_api.categories.dtos.UpdateCategoryDto;
import ec.edu.ups.academic_events_api.categories.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> findOne(
            @PathVariable Long id) {

        return ResponseEntity.ok(categoryService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(
            @Valid @RequestBody CreateCategoryDto dto) {

        CategoryResponseDto createdCategory = categoryService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryDto dto) {

        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);

        return ResponseEntity.noContent().build();
    }
}