package ec.edu.ups.academic_events_api.security.services;

import ec.edu.ups.academic_events_api.security.dtos.AuthResponseDto;
import ec.edu.ups.academic_events_api.security.dtos.LoginRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterResponseDto;

public interface AuthService {

    RegisterResponseDto register(RegisterRequestDto dto);

    AuthResponseDto login(LoginRequestDto dto);
}