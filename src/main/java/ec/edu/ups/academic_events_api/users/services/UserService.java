package ec.edu.ups.academic_events_api.users.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ec.edu.ups.academic_events_api.users.dtos.UpdateUserRolesDto;
import ec.edu.ups.academic_events_api.users.dtos.UpdateUserStatusDto;
import ec.edu.ups.academic_events_api.users.dtos.UserResponseDto;

public interface UserService {

        Page<UserResponseDto> findAll(Pageable pageable);

        UserResponseDto findById(Long id);

        UserResponseDto updateStatus(
                        Long id,
                        UpdateUserStatusDto dto);

        UserResponseDto updateRoles(
                        Long id,
                        UpdateUserRolesDto dto);
}
