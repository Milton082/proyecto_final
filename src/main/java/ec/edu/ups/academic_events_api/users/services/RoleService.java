package ec.edu.ups.academic_events_api.users.services;

import java.util.List;

import ec.edu.ups.academic_events_api.users.dtos.RoleResponseDto;

public interface RoleService {

    List<RoleResponseDto> findAll();
}