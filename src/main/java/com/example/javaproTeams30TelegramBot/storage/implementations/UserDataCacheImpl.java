package com.example.javaproTeams30TelegramBot.storage.implementations;

import com.example.javaproTeams30TelegramBot.storage.interfaces.UserDataCache;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserDataCacheImpl implements UserDataCache {

    private final Map<Long, String> userAuthData = new HashMap<>();

    @Override
    public void storeUserData(Long userId, String email) {
        userAuthData.put(userId, email);
    }

    @Override
    public String getUserData(Long userId) {
        return userAuthData.getOrDefault(userId, null);
    }

    @Override
    public void clearUserData(Long userId) {
        userAuthData.remove(userId);
    }
}
