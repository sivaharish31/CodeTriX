package com.codetrix.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@EnableScheduling
public class TokenBlacklistService {

    private final Map<String, Date> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklistToken(String tokenId, Date expirationDate) {
        blacklistedTokens.put(tokenId, expirationDate);
        log.debug("Token blacklisted: {}", tokenId);
    }

    public boolean isBlacklisted(String tokenId) {
        return blacklistedTokens.containsKey(tokenId);
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        Date now = new Date();
        int removedCount = 0;

        var iterator = blacklistedTokens.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().before(now)) {
                iterator.remove();
                removedCount++;
            }
        }

        if (removedCount > 0) {
            log.info("Cleaned up {} expired blacklisted tokens", removedCount);
        }
    }

    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }
}
