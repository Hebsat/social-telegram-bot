package com.example.javaproTeams30TelegramBot.storage.implementations;

import com.example.javaproTeams30TelegramBot.model.AuthStates;
import com.example.javaproTeams30TelegramBot.storage.interfaces.AuthStatesCache;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class StatesCacheImpl implements AuthStatesCache {

    private final Map<Long, AuthStates> authStates = new HashMap<>();

    @Override
    public void setAuthState(Long userId, AuthStates state) {
        authStates.put(userId, state);
    }

    @Override
    public AuthStates getAuthState(Long userId) {
        return authStates.getOrDefault(userId, AuthStates.UNAUTHORIZED);
    }

    @Override
    public boolean containsAuthState(Long userId) {
        return authStates.containsKey(userId);
    }
}
