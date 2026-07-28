package ec.edu.ups.academic_events_api.security.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.academic_events_api.core.exceptions.domain.BadRequestException;
import ec.edu.ups.academic_events_api.security.config.JwtProperties;
import ec.edu.ups.academic_events_api.security.entities.RefreshTokenEntity;
import ec.edu.ups.academic_events_api.security.repositories.RefreshTokenRepository;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;

@Service
public class RefreshTokenServiceImpl
                implements RefreshTokenService {

        private static final String REVOKED_PREFIX = "revoked-refresh-token:";

        private final RefreshTokenRepository refreshTokenRepository;
        private final JwtProperties jwtProperties;
        private final StringRedisTemplate redisTemplate;

        public RefreshTokenServiceImpl(
                        RefreshTokenRepository refreshTokenRepository,
                        JwtProperties jwtProperties,
                        StringRedisTemplate redisTemplate) {
                this.refreshTokenRepository = refreshTokenRepository;
                this.jwtProperties = jwtProperties;
                this.redisTemplate = redisTemplate;
        }

        @Override
        @Transactional
        public String create(
                        UserEntity user,
                        String clientIp) {
                UUID tokenId = UUID.randomUUID();
                String secret = UUID.randomUUID()
                                + "."
                                + UUID.randomUUID();

                String rawToken = tokenId + "." + secret;

                RefreshTokenEntity entity = new RefreshTokenEntity();
                entity.setTokenId(tokenId);
                entity.setUser(user);
                entity.setTokenHash(hash(rawToken));
                entity.setExpiresAt(
                                OffsetDateTime.now(ZoneOffset.UTC)
                                                .plusNanos(
                                                                jwtProperties.refreshExpiration()
                                                                                * 1_000_000));
                entity.setCreatedByIp(clientIp);

                refreshTokenRepository.save(entity);

                return rawToken;
        }

        @Override
        @Transactional(readOnly = true)
        public RefreshTokenEntity validate(String rawToken) {
                UUID tokenId = extractTokenId(rawToken);

                String redisKey = REVOKED_PREFIX + tokenId;

                if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                        throw new BadRequestException(
                                        "El refresh token fue revocado");
                }

                RefreshTokenEntity entity = refreshTokenRepository
                                .findByTokenId(tokenId)
                                .orElseThrow(() -> new BadRequestException(
                                                "Refresh token inválido"));

                if (!MessageDigest.isEqual(
                                entity.getTokenHash()
                                                .getBytes(StandardCharsets.UTF_8),
                                hash(rawToken)
                                                .getBytes(StandardCharsets.UTF_8))) {
                        throw new BadRequestException(
                                        "Refresh token inválido");
                }

                if (entity.isRevoked()) {
                        throw new BadRequestException(
                                        "El refresh token fue revocado");
                }

                if (entity.isExpired()) {
                        throw new BadRequestException(
                                        "El refresh token expiró");
                }

                return entity;
        }

        @Override
        @Transactional
        public String rotate(
                        RefreshTokenEntity currentToken,
                        String clientIp) {
                String newRawToken = create(
                                currentToken.getUser(),
                                clientIp);

                UUID newTokenId = extractTokenId(newRawToken);

                currentToken.setRevokedAt(
                                OffsetDateTime.now(ZoneOffset.UTC));
                currentToken.setReplacedByTokenId(newTokenId);

                refreshTokenRepository.save(currentToken);

                saveRevokedTokenInRedis(currentToken);

                return newRawToken;
        }

        @Override
        @Transactional
        public void revoke(String rawToken) {
                RefreshTokenEntity entity = validate(rawToken);

                entity.setRevokedAt(
                                OffsetDateTime.now(ZoneOffset.UTC));

                refreshTokenRepository.save(entity);

                saveRevokedTokenInRedis(entity);
        }

        private void saveRevokedTokenInRedis(
                        RefreshTokenEntity entity) {
                long ttlSeconds = java.time.Duration.between(
                                OffsetDateTime.now(ZoneOffset.UTC),
                                entity.getExpiresAt()).getSeconds();

                if (ttlSeconds <= 0) {
                        return;
                }

                redisTemplate.opsForValue().set(
                                REVOKED_PREFIX + entity.getTokenId(),
                                "true",
                                java.time.Duration.ofSeconds(ttlSeconds));
        }

        private UUID extractTokenId(String rawToken) {
                try {
                        int separatorIndex = rawToken.indexOf('.');

                        if (separatorIndex <= 0) {
                                throw new IllegalArgumentException();
                        }

                        return UUID.fromString(
                                        rawToken.substring(0, separatorIndex));

                } catch (Exception exception) {
                        throw new BadRequestException(
                                        "Refresh token inválido");
                }
        }

        private String hash(String value) {
                try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");

                        byte[] hashedBytes = digest.digest(
                                        value.getBytes(StandardCharsets.UTF_8));

                        return Base64.getUrlEncoder()
                                        .withoutPadding()
                                        .encodeToString(hashedBytes);

                } catch (NoSuchAlgorithmException exception) {
                        throw new IllegalStateException(
                                        "No se pudo procesar el refresh token",
                                        exception);
                }
        }
}