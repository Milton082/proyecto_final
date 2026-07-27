package ec.edu.ups.academic_events_api.users.mappers;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ec.edu.ups.academic_events_api.users.dtos.UserResponseDto;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import ec.edu.ups.academic_events_api.users.enums.RoleName;

@Component
public class UserMapper {

    public UserResponseDto toResponse(UserEntity entity) {

        Set<RoleName> roles = entity.getRoles()
                .stream()
                .map(role -> role.getName())
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return new UserResponseDto(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getStatus(),
                roles,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}