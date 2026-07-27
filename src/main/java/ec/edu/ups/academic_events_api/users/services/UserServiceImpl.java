package ec.edu.ups.academic_events_api.users.services;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.academic_events_api.core.exceptions.domain.NotFoundException;
import ec.edu.ups.academic_events_api.users.dtos.UpdateUserRolesDto;
import ec.edu.ups.academic_events_api.users.dtos.UpdateUserStatusDto;
import ec.edu.ups.academic_events_api.users.dtos.UserResponseDto;
import ec.edu.ups.academic_events_api.users.entities.RoleEntity;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import ec.edu.ups.academic_events_api.users.enums.RoleName;
import ec.edu.ups.academic_events_api.users.mappers.UserMapper;
import ec.edu.ups.academic_events_api.users.repositories.RoleRepository;
import ec.edu.ups.academic_events_api.users.repositories.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public UserResponseDto findById(Long id) {
        UserEntity user = findEntityById(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateStatus(
            Long id,
            UpdateUserStatusDto dto) {
        UserEntity user = findEntityById(id);

        user.setStatus(dto.status());

        UserEntity updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateRoles(
            Long id,
            UpdateUserRolesDto dto) {
        UserEntity user = findEntityById(id);

        Set<RoleEntity> roles = dto.roles()
                .stream()
                .sorted()
                .map(this::findRoleByName)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        user.setRoles(roles);

        UserEntity updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    private UserEntity findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "No existe un usuario con id " + id));
    }

    private RoleEntity findRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException(
                        "No existe el rol " + roleName));
    }
}