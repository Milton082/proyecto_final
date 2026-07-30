package ec.edu.ups.academic_events_api.security.services;

public interface LoginProtectionService {

    void validateLoginAllowed(
            String email,
            String clientIp);

    void registerFailure(
            String email,
            String clientIp);

    void registerSuccess(
            String email,
            String clientIp);
}