package com.example.javaproTeams30TelegramBot.storage.interfaces;

import com.example.javaproTeams30TelegramBot.model.DataContainer;

public interface UserDialogsDataCache {

    void storeUserDialogsData(Long userId, DataContainer<?> data);

    DataContainer<?> getUserDialogsData(Long userId);

    void clearUserDialogsData(Long userId);
}
