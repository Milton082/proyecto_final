package ec.edu.ups.academic_events_api.security.services;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.academic_events_api.core.audit.enums.AuditAction;
import ec.edu.ups.academic_events_api.core.audit.enums.AuditResult;
import ec.edu.ups.academic_events_api.core.audit.services.AuditService;
import ec.edu.ups.academic_events_api.core.dtos.MessageResponseDto;
import ec.edu.ups.academic_events_api.core.exceptions.domain.ConflictException;
import ec.edu.ups.academic_events_api.core.exceptions.domain.NotFoundException;
import ec.edu.ups.academic_events_api.core.exceptions.domain.UnauthorizedException;
import ec.edu.ups.academic_events_api.security.config.JwtProperties;
import ec.edu.ups.academic_events_api.security.dtos.AuthResponseDto;
import ec.edu.ups.academic_events_api.security.dtos.CurrentUserResponseDto;
import ec.edu.ups.academic_events_api.security.dtos.LoginRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.LogoutRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RefreshTokenRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterResponseDto;
import ec.edu.ups.academic_events_api.security.entities.RefreshTokenEntity;
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
        private final RefreshTokenService refreshTokenService;
        private final LoginProtectionService loginProtectionService;
        private final AuditService auditService;

        public AuthServiceImpl(
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtUtil jwtUtil,
                        JwtProperties jwtProperties,
                        RefreshTokenService refreshTokenService,
                        LoginProtectionService loginProtectionService,
                        AuditService auditService) {
                this.userRepository = userRepository;
                this.roleRepository = roleRepository;
                this.passwordEncoder = passwordEncoder;
                this.authenticationManager = authenticationManager;
                this.jwtUtil = jwtUtil;
                this.jwtProperties = jwtProperties;
                this.refreshTokenService = refreshTokenService;
                this.loginProtectionService = loginProtectionService;
                this.auditService = auditService;
        }

        @Override
        @Transactional
        public RegisterResponseDto register(
                        RegisterRequestDto dto) {
                String normalizedEmail = dto.email()
                                .trim()
                                .toLowerCase();

                if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                        throw new ConflictException(
                                        "Account_Already_Exists",
                                        "No fue posible completar el registro con los datos ingresados");
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
                user.setRoles(
                                new HashSet<>(Set.of(participantRole)));

                UserEntity savedUser = userRepository.save(user);
                auditService.register(
                                null,
                                AuditAction.ACCOUNT_REGISTERED,
                                "USER",
                                savedUser.getId(),
                                null,
                                Map.of(
                                                "email", savedUser.getEmail(),
                                                "status", savedUser.getStatus().name(),
                                                "roles", Set.of(RoleName.PARTICIPANT)),
                                AuditResult.SUCCESS);

                return new RegisterResponseDto(
                                savedUser.getId(),
                                savedUser.getFirstName(),
                                savedUser.getLastName(),
                                savedUser.getEmail(),
                                savedUser.getStatus(),
                                Set.of(RoleName.PARTICIPANT));
        }

        @Override
        @Transactional
        public AuthResponseDto login(
                        LoginRequestDto dto,
                        String clientIp) {
                String normalizedEmail = dto.email()
                                .trim()
                                .toLowerCase();

                loginProtectionService.validateLoginAllowed(
                                normalizedEmail,
                                clientIp);

                Authentication authentication;

                try {
                        authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        normalizedEmail,
                                                        dto.password()));

                } catch (AuthenticationException exception) {

                        auditService.register(
                                        null,
                                        AuditAction.LOGIN_FAILED,
                                        "USER",
                                        null,
                                        null,
                                        Map.of(
                                                        "email",
                                                        normalizedEmail),
                                        AuditResult.FAILED);

                        loginProtectionService.registerFailure(
                                        normalizedEmail,
                                        clientIp);

                        throw new UnauthorizedException(
                                        "Correo o contraseña incorrectos");
                }

                loginProtectionService.registerSuccess(
                                normalizedEmail,
                                clientIp);

                UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

                UserEntity user = userRepository
                                .findById(principal.getId())
                                .orElseThrow(() -> new UnauthorizedException(
                                                "Correo o contraseña incorrectos"));

                String accessToken = jwtUtil.generateAccessToken(principal);

                String refreshToken = refreshTokenService.create(
                                user,
                                clientIp);

                Set<RoleName> roles = principal.getAuthorities()
                                .stream()
                                .map(authority -> authority
                                                .getAuthority()
                                                .replaceFirst("^ROLE_", ""))
                                .map(RoleName::valueOf)
                                .collect(Collectors.toCollection(
                                                LinkedHashSet::new));

                auditService.register(
                                principal.getId(),
                                AuditAction.LOGIN_SUCCESS,
                                "USER",
                                principal.getId(),
                                null,
                                Map.of(
                                                "email", principal.getUsername(),
                                                "roles", roles),
                                AuditResult.SUCCESS);

                return new AuthResponseDto(
                                accessToken,
                                refreshToken,
                                "Bearer",
                                jwtProperties.accessExpiration(),
                                jwtProperties.refreshExpiration(),
                                principal.getId(),
                                principal.getUsername(),
                                roles);
        }

        @Override
        @Transactional
        public AuthResponseDto refresh(
                        RefreshTokenRequestDto dto,
                        String clientIp) {
                RefreshTokenEntity currentToken = refreshTokenService.validate(
                                dto.refreshToken());

                UserEntity user = currentToken.getUser();

                UserDetailsImpl principal = new UserDetailsImpl(user);

                String newAccessToken = jwtUtil.generateAccessToken(principal);

                String newRefreshToken = refreshTokenService.rotate(
                                currentToken,
                                clientIp);

                Set<RoleName> roles = user.getRoles()
                                .stream()
                                .map(RoleEntity::getName)
                                .sorted()
                                .collect(Collectors.toCollection(
                                                LinkedHashSet::new));

                auditService.register(
                                user.getId(),
                                AuditAction.REFRESH_TOKEN_SUCCESS,
                                "USER",
                                user.getId(),
                                null,
                                Map.of("email", user.getEmail()),
                                AuditResult.SUCCESS);
                return new AuthResponseDto(
                                newAccessToken,
                                newRefreshToken,
                                "Bearer",
                                jwtProperties.accessExpiration(),
                                jwtProperties.refreshExpiration(),
                                user.getId(),
                                user.getEmail(),
                                roles);
        }

        @Override
        @Transactional
        public MessageResponseDto logout(
                        LogoutRequestDto dto) {
                Long actorId = getAuthenticatedUserId();

                refreshTokenService.revoke(
                                dto.refreshToken());

                auditService.register(
                                actorId,
                                AuditAction.LOGOUT_SUCCESS,
                                "USER",
                                actorId,
                                null,
                                Map.of("sessionClosed", true),
                                AuditResult.SUCCESS);

                return new MessageResponseDto(
                                "Sesión cerrada correctamente");
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
                                .sorted()
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

        private Long getAuthenticatedUserId() {
                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication != null
                                && authentication.getPrincipal() instanceof UserDetailsImpl principal) {

                        return principal.getId();
                }

                return null;
        }
}