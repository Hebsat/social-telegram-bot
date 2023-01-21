package com.example.javaproTeams30TelegramBot.storage.implementations;

import com.example.javaproTeams30TelegramBot.storage.interfaces.UserTokenCache;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserTokenCacheImpl implements UserTokenCache {

    private final Map<Long, String> userTokens = new HashMap<>();

    @Override
    public void storeToken(Long userId, String token) {
        userTokens.put(userId, token);
    }

    @Override
    public String getToken(Long userId) {
        return userTokens.getOrDefault(userId, null);
    }
}
