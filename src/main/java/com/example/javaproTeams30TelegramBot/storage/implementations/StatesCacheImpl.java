package com.example.javaproTeams30TelegramBot.storage.implementations;

import com.example.javaproTeams30TelegramBot.model.AuthStates;
import com.example.javaproTeams30TelegramBot.model.OtherStates;
import com.example.javaproTeams30TelegramBot.storage.interfaces.AuthStatesCache;
import com.example.javaproTeams30TelegramBot.storage.interfaces.OtherStatesCache;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class StatesCacheImpl implements AuthStatesCache, OtherStatesCache {

    private final Map<Long, AuthStates> authStates = new HashMap<>();
    private final Map<Long, OtherStates> otherStates = new HashMap<>();

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

    @Override
    public void setCurrentState(Long userId, OtherStates state) {
        otherStates.put(userId, state);
    }

    @Override
    public OtherStates getCurrentState(Long userId) {
        return otherStates.getOrDefault(userId, OtherStates.OPEN_STATE);
    }

    @Override
    public void removeUsersStates(Long userId) {
        otherStates.remove(userId);
    }
}
