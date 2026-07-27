package ec.edu.ups.academic_events_api.users.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.academic_events_api.users.dtos.UpdateUserRolesDto;
import ec.edu.ups.academic_events_api.users.dtos.UpdateUserStatusDto;
import ec.edu.ups.academic_events_api.users.dtos.UserResponseDto;
import ec.edu.ups.academic_events_api.users.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(
                userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                userService.findById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusDto dto) {
        return ResponseEntity.ok(
                userService.updateStatus(id, dto));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserResponseDto> updateRoles(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRolesDto dto) {
        return ResponseEntity.ok(
                userService.updateRoles(id, dto));
    }
}