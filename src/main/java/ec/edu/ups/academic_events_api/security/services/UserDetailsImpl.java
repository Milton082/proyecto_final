package ec.edu.ups.academic_events_api.security.services;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import ec.edu.ups.academic_events_api.users.enums.UserStatus;

public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final boolean active;
    private final List<GrantedAuthority> authorities;

    public UserDetailsImpl(UserEntity user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.active = user.getStatus() == UserStatus.ACTIVE;

        this.authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(
                        "ROLE_" + role.getName().name()))
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}