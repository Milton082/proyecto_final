package ec.edu.ups.academic_events_api.security.services;

import ec.edu.ups.academic_events_api.security.dtos.RegisterRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterResponseDto;

public interface AuthService {

    RegisterResponseDto register(RegisterRequestDto dto);
}