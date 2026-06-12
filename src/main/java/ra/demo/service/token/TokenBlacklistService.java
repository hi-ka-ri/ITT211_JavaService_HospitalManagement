package ra.demo.service.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {
    private final StringRedisTemplate redisTemplate;
    private final Map<String, LocalDateTime> fallbackBlacklist = new ConcurrentHashMap<>();

    @Value("${app.token-blacklist.prefix:hospital:blacklist:access:}")
    private String prefix;

    public void blacklist(String token, LocalDateTime expiredAt) {
        if (!StringUtils.hasText(token) || expiredAt == null) return;
        Duration ttl = Duration.between(LocalDateTime.now(), expiredAt);
        if (ttl.isNegative() || ttl.isZero()) return;
        try {
            redisTemplate.opsForValue().set(key(token), "revoked", ttl);
            log.info("[REDIS] Blacklisted access token with ttl={} seconds", ttl.toSeconds());
        } catch (RedisConnectionFailureException ex) {
            fallbackBlacklist.put(token, expiredAt);
            log.warn("Redis unavailable. Token blacklist is stored in memory for current runtime only: {}", ex.getMessage());
        }
    }

    public boolean isBlacklisted(String token) {
        if (!StringUtils.hasText(token)) return false;
        removeExpiredFallbackTokens();
        LocalDateTime fallbackExpiredAt = fallbackBlacklist.get(token);
        if (fallbackExpiredAt != null && fallbackExpiredAt.isAfter(LocalDateTime.now())) return true;
        try {
            Boolean exists = redisTemplate.hasKey(key(token));
            return Boolean.TRUE.equals(exists);
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis unavailable while checking blacklist. Only in-memory fallback is used: {}", ex.getMessage());
            return false;
        }
    }

    private void removeExpiredFallbackTokens() {
        LocalDateTime now = LocalDateTime.now();
        fallbackBlacklist.entrySet().removeIf(e -> !e.getValue().isAfter(now));
    }

    private String key(String token) {
        return prefix + token;
    }
}
