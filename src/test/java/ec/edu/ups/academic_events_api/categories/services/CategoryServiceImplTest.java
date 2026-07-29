package ec.edu.ups.academic_events_api.categories.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.ups.academic_events_api.categories.dtos.CreateCategoryDto;
import ec.edu.ups.academic_events_api.categories.repositories.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService =
                new CategoryServiceImpl(categoryRepository);
    }

    @Test
    @DisplayName(
            "Debe rechazar la creación de una categoría duplicada"
    )
    void createShouldRejectDuplicatedCategory() {
        CreateCategoryDto dto = new CreateCategoryDto();
        dto.setName("Desarrollo de Software");
        dto.setDescription(
                "Categoría relacionada con programación"
        );

        when(categoryRepository.existsByNameIgnoreCase(
                "Desarrollo de Software"
        )).thenReturn(true);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> categoryService.create(dto)
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        assertEquals(
                "Ya existe una categoría con el nombre indicado",
                exception.getReason()
        );

        verify(categoryRepository)
                .existsByNameIgnoreCase(
                        "Desarrollo de Software"
                );

        verify(categoryRepository, never()).save(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    @DisplayName(
            "Debe normalizar el nombre antes de verificar duplicados"
    )
    void createShouldTrimNameBeforeCheckingDuplicate() {
        CreateCategoryDto dto = new CreateCategoryDto();
        dto.setName("  Ciberseguridad  ");
        dto.setDescription("Seguridad informática");

        when(categoryRepository.existsByNameIgnoreCase(
                "Ciberseguridad"
        )).thenReturn(true);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> categoryService.create(dto)
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(categoryRepository)
                .existsByNameIgnoreCase("Ciberseguridad");

        verify(categoryRepository, never()).save(
                org.mockito.ArgumentMatchers.any()
        );
    }
}