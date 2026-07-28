package ec.edu.ups.academic_events_api.security.services;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.academic_events_api.core.exceptions.domain.ConflictException;
import ec.edu.ups.academic_events_api.core.exceptions.domain.NotFoundException;
import ec.edu.ups.academic_events_api.security.config.JwtProperties;
import ec.edu.ups.academic_events_api.security.dtos.AuthResponseDto;
import ec.edu.ups.academic_events_api.security.dtos.CurrentUserResponseDto;
import ec.edu.ups.academic_events_api.security.dtos.LoginRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterResponseDto;
import ec.edu.ups.academic_events_api.security.utils.JwtUtil;
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
        private final AuthenticationManager authenticationManager;
        private final JwtUtil jwtUtil;
        private final JwtProperties jwtProperties;

        public AuthServiceImpl(
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtUtil jwtUtil,
                        JwtProperties jwtProperties) {
                this.userRepository = userRepository;
                this.roleRepository = roleRepository;
                this.passwordEncoder = passwordEncoder;
                this.authenticationManager = authenticationManager;
                this.jwtUtil = jwtUtil;
                this.jwtProperties = jwtProperties;
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

        @Override
        public AuthResponseDto login(LoginRequestDto dto) {

                String normalizedEmail = dto.email()
                                .trim()
                                .toLowerCase();

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                normalizedEmail,
                                                dto.password()));

                UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

                String accessToken = jwtUtil.generateAccessToken(principal);

                Set<RoleName> roles = principal.getAuthorities()
                                .stream()
                                .map(authority -> authority.getAuthority()
                                                .replaceFirst("^ROLE_", ""))
                                .map(RoleName::valueOf)
                                .collect(Collectors.toCollection(
                                                LinkedHashSet::new));

                return new AuthResponseDto(
                                accessToken,
                                "Bearer",
                                jwtProperties.accessExpiration(),
                                principal.getId(),
                                principal.getUsername(),
                                roles);
        }

        @Override
        @Transactional(readOnly = true)
        public CurrentUserResponseDto currentUser() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null
                                || !(authentication.getPrincipal() instanceof UserDetailsImpl principal)) {

                        throw new IllegalStateException(
                                        "No existe un usuario autenticado");
                }

                UserEntity user = userRepository
                                .findById(principal.getId())
                                .orElseThrow(() -> new NotFoundException(
                                                "El usuario autenticado no existe"));

                Set<RoleName> roles = user.getRoles()
                                .stream()
                                .map(RoleEntity::getName)
                                .collect(Collectors.toCollection(
                                                LinkedHashSet::new));

                return new CurrentUserResponseDto(
                                user.getId(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getEmail(),
                                user.getStatus(),
                                roles);
        }
}