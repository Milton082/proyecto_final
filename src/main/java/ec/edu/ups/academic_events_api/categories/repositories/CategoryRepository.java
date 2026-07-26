package ec.edu.ups.academic_events_api.categories.repositories;

import ec.edu.ups.academic_events_api.categories.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByActiveTrueOrderByNameAsc();
    Optional<CategoryEntity> findByIdAndActiveTrue(Long id);
    Optional<CategoryEntity> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}