package com.example.javaproTeams30TelegramBot.storage.interfaces;

public interface UserTokenCache {

    void storeToken(Long userId, String token);

    String getToken(Long userId);

    void removeToken(Long userId);
}
