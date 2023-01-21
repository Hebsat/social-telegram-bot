package com.example.javaproTeams30TelegramBot.storage.interfaces;

import com.example.javaproTeams30TelegramBot.model.BotStates;

public interface BotStatesCache {

    void setState(Long userId, BotStates state);

    BotStates getBotState(Long userId);

    boolean containsStatus(Long userId);
}
