package com.example.javaproTeams30TelegramBot.storage.interfaces;

import com.example.javaproTeams30TelegramBot.model.SettingsStates;

public interface SettingsStatesCache {

    void setCurrentSettingsState(Long userId, SettingsStates state);

    SettingsStates getCurrentSettingsState(Long userId);

    void removeCurrentSettingsStates(Long userId);
}
