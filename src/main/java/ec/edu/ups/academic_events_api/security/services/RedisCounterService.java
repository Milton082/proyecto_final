package ec.edu.ups.academic_events_api.security.services;

public interface RedisCounterService {

    long incrementWithExpiration(
            String key,
            long expirationSeconds);

    void delete(String key);

    boolean exists(String key);

    void setWithExpiration(
            String key,
            String value,
            long expirationSeconds);

    Long getExpirationSeconds(String key);
}