package ec.edu.ups.academic_events_api.security.services;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class RedisCounterServiceImpl
        implements RedisCounterService {

    private static final String INCREMENT_SCRIPT = """
            local current = redis.call('INCR', KEYS[1])

            if current == 1 then
                redis.call('EXPIRE', KEYS[1], ARGV[1])
            end

            return current
            """;

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> incrementScript;

    public RedisCounterServiceImpl(
            StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        this.incrementScript = new DefaultRedisScript<>();
        this.incrementScript.setScriptText(INCREMENT_SCRIPT);
        this.incrementScript.setResultType(Long.class);
    }

    @Override
    public long incrementWithExpiration(
            String key,
            long expirationSeconds) {
        Long result = redisTemplate.execute(
                incrementScript,
                Collections.singletonList(key),
                String.valueOf(expirationSeconds));

        if (result == null) {
            throw new IllegalStateException(
                    "No se pudo actualizar el contador de seguridad");
        }

        return result;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key));
    }

    @Override
    public void setWithExpiration(
            String key,
            String value,
            long expirationSeconds) {
        redisTemplate.opsForValue().set(
                key,
                value,
                Duration.ofSeconds(expirationSeconds));
    }

    @Override
    public Long getExpirationSeconds(String key) {
        return redisTemplate.getExpire(
                key,
                TimeUnit.SECONDS);
    }
}
