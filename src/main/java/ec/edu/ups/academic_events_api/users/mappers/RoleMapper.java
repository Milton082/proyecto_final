package ec.edu.ups.academic_events_api.users.mappers;

import org.springframework.stereotype.Component;

import ec.edu.ups.academic_events_api.users.dtos.RoleResponseDto;
import ec.edu.ups.academic_events_api.users.entities.RoleEntity;

@Component
public class RoleMapper {

    public RoleResponseDto toResponse(RoleEntity entity) {
        return new RoleResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt());
    }
}