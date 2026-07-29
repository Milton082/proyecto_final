package ec.edu.ups.academic_events_api.security.services;

import ec.edu.ups.academic_events_api.core.dtos.MessageResponseDto;
import ec.edu.ups.academic_events_api.security.dtos.AuthResponseDto;
import ec.edu.ups.academic_events_api.security.dtos.CurrentUserResponseDto;
import ec.edu.ups.academic_events_api.security.dtos.LoginRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.LogoutRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RefreshTokenRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterResponseDto;

public interface AuthService {

    RegisterResponseDto register(RegisterRequestDto dto);

    AuthResponseDto login(
            LoginRequestDto dto,
            String clientIp);

    AuthResponseDto refresh(
            RefreshTokenRequestDto dto,
            String clientIp);

    MessageResponseDto logout(LogoutRequestDto dto);

    CurrentUserResponseDto currentUser();
}