package com.example.javaproTeams30TelegramBot.storage.interfaces;

public interface UserAuthDataCache {

    void storeUserAuthData(Long userId, String email);

    String getUserAuthData(Long userId);

    void clearUserAuthData(Long userId);
}
