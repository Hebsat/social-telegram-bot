package com.example.javaproTeams30TelegramBot.storage.interfaces;

import com.example.javaproTeams30TelegramBot.model.AuthStates;

public interface AuthStatesCache {

    void setAuthState(Long userId, AuthStates state);

    AuthStates getAuthState(Long userId);

    boolean containsAuthState(Long userId);
}
