package com.example.javaproTeams30TelegramBot.storage.interfaces;

import com.example.javaproTeams30TelegramBot.model.OtherStates;

public interface OtherStatesCache {

    void setCurrentState(Long userId, OtherStates state);

    OtherStates getCurrentState(Long userId);

    void removeUsersStates(Long userId);
}
