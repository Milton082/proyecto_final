package ec.edu.ups.academic_events_api.security.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.academic_events_api.core.exceptions.domain.ConflictException;
import ec.edu.ups.academic_events_api.core.exceptions.domain.NotFoundException;
import ec.edu.ups.academic_events_api.security.dtos.RegisterRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterResponseDto;
import ec.edu.ups.academic_events_api.users.entities.RoleEntity;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import ec.edu.ups.academic_events_api.users.enums.RoleName;
import ec.edu.ups.academic_events_api.users.enums.UserStatus;
import ec.edu.ups.academic_events_api.users.repositories.RoleRepository;
import ec.edu.ups.academic_events_api.users.repositories.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto dto) {

        String normalizedEmail = dto.email()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException(
                    "Ya existe un usuario con el correo ingresado");
        }

        RoleEntity participantRole = roleRepository
                .findByName(RoleName.PARTICIPANT)
                .orElseThrow(() -> new NotFoundException(
                        "No se encontró el rol PARTICIPANT"));

        UserEntity user = new UserEntity();
        user.setFirstName(dto.firstName().trim());
        user.setLastName(dto.lastName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(
                passwordEncoder.encode(dto.password()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(new HashSet<>(Set.of(participantRole)));

        UserEntity savedUser = userRepository.save(user);

        return new RegisterResponseDto(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getStatus(),
                Set.of(RoleName.PARTICIPANT));
    }
}