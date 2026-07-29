package ec.edu.ups.academic_events_api.security.services;

import ec.edu.ups.academic_events_api.security.entities.RefreshTokenEntity;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;

public interface RefreshTokenService {

    String create(
            UserEntity user,
            String clientIp);

    RefreshTokenEntity validate(String rawToken);

    String rotate(
            RefreshTokenEntity currentToken,
            String clientIp);

    void revoke(String rawToken);
}