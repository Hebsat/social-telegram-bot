package com.example.javaproTeams30TelegramBot.storage.interfaces;

public interface UserDataCache {

    void storeUserData(Long userId, String email);

    String getUserData(Long userId);

    void clearUserData(Long userId);
}
