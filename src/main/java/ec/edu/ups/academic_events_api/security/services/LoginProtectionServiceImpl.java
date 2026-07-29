package ec.edu.ups.academic_events_api.security.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Service;

import ec.edu.ups.academic_events_api.core.exceptions.domain.TooManyRequestsException;
import ec.edu.ups.academic_events_api.security.config.LoginSecurityProperties;

@Service
public class LoginProtectionServiceImpl
        implements LoginProtectionService {

    private static final String RATE_LIMIT_PREFIX = "auth:login:rate:";

    private static final String FAILED_ATTEMPTS_PREFIX = "auth:login:failed:";

    private static final String BLOCK_PREFIX = "auth:login:blocked:";

    private static final String GENERIC_LIMIT_MESSAGE = "Demasiados intentos de inicio de sesión. "
            + "Intente nuevamente más tarde";

    private final RedisCounterService redisCounterService;
    private final LoginSecurityProperties properties;

    public LoginProtectionServiceImpl(
            RedisCounterService redisCounterService,
            LoginSecurityProperties properties) {
        this.redisCounterService = redisCounterService;
        this.properties = properties;
    }

    @Override
    public void validateLoginAllowed(
            String email,
            String clientIp) {
        String safeEmail = normalizeEmail(email);
        String safeIp = normalizeIp(clientIp);

        validateIpRateLimit(safeIp);
        validateTemporaryBlock(safeEmail, safeIp);
    }

    @Override
    public void registerFailure(
            String email,
            String clientIp) {
        String credentialFingerprint = credentialFingerprint(email, clientIp);

        String failedAttemptsKey = FAILED_ATTEMPTS_PREFIX
                + credentialFingerprint;

        long failedAttempts = redisCounterService.incrementWithExpiration(
                failedAttemptsKey,
                properties.failedAttemptWindowSeconds());

        if (failedAttempts >= properties.maxFailedAttempts()) {

            String blockKey = BLOCK_PREFIX + credentialFingerprint;

            redisCounterService.setWithExpiration(
                    blockKey,
                    "blocked",
                    properties.blockDurationSeconds());

            redisCounterService.delete(failedAttemptsKey);

            throw new TooManyRequestsException(
                    GENERIC_LIMIT_MESSAGE);
        }
    }

    @Override
    public void registerSuccess(
            String email,
            String clientIp) {
        String credentialFingerprint = credentialFingerprint(email, clientIp);

        redisCounterService.delete(
                FAILED_ATTEMPTS_PREFIX
                        + credentialFingerprint);
    }

    private void validateIpRateLimit(String clientIp) {
        String ipFingerprint = hash(clientIp);

        String rateLimitKey = RATE_LIMIT_PREFIX + ipFingerprint;

        long requestCount = redisCounterService.incrementWithExpiration(
                rateLimitKey,
                properties.rateLimitWindowSeconds());

        if (requestCount > properties.rateLimitMaxRequests()) {

            throw new TooManyRequestsException(
                    GENERIC_LIMIT_MESSAGE);
        }
    }

    private void validateTemporaryBlock(
            String email,
            String clientIp) {
        String blockKey = BLOCK_PREFIX
                + credentialFingerprint(
                        email,
                        clientIp);

        if (redisCounterService.exists(blockKey)) {
            throw new TooManyRequestsException(
                    GENERIC_LIMIT_MESSAGE);
        }
    }

    private String credentialFingerprint(
            String email,
            String clientIp) {
        return hash(
                normalizeEmail(email)
                        + "|"
                        + normalizeIp(clientIp));
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "unknown-email";
        }

        return email.trim().toLowerCase();
    }

    private String normalizeIp(String clientIp) {
        if (clientIp == null || clientIp.isBlank()) {
            return "unknown-ip";
        }

        return clientIp.trim();
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "No se pudo generar la clave de seguridad",
                    exception);
        }
    }
}