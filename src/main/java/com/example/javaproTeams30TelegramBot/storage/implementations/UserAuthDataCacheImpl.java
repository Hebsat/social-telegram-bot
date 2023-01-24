package com.example.javaproTeams30TelegramBot.storage.implementations;

import com.example.javaproTeams30TelegramBot.model.DataContainer;
import com.example.javaproTeams30TelegramBot.storage.interfaces.UserAuthDataCache;
import com.example.javaproTeams30TelegramBot.storage.interfaces.UserDialogsDataCache;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserAuthDataCacheImpl implements UserAuthDataCache, UserDialogsDataCache {

    private final Map<Long, String> userAuthData = new HashMap<>();
    private final Map<Long, DataContainer<?>> usersDialogsData = new HashMap<>();

    @Override
    public void storeUserAuthData(Long userId, String email) {
        userAuthData.put(userId, email);
    }

    @Override
    public String getUserAuthData(Long userId) {
        return userAuthData.getOrDefault(userId, null);
    }

    @Override
    public void clearUserAuthData(Long userId) {
        userAuthData.remove(userId);
    }

    @Override
    public void storeUserDialogsData(Long userId, DataContainer<?> data) {
        usersDialogsData.put(userId, data);
    }

    @Override
    public DataContainer<?> getUserDialogsData(Long userId) {
        return usersDialogsData.get(userId);
    }

    @Override
    public void clearUserDialogsData(Long userId) {
        usersDialogsData.remove(userId);
    }
}
