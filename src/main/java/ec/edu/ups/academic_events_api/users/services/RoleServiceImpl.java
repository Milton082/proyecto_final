package ec.edu.ups.academic_events_api.users.services;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.academic_events_api.users.dtos.RoleResponseDto;
import ec.edu.ups.academic_events_api.users.mappers.RoleMapper;
import ec.edu.ups.academic_events_api.users.repositories.RoleRepository;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(
            RoleRepository roleRepository,
            RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<RoleResponseDto> findAll() {
        return roleRepository
                .findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }
}