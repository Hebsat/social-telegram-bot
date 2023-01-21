package com.example.javaproTeams30TelegramBot.storage.implementations;

import com.example.javaproTeams30TelegramBot.model.BotStates;
import com.example.javaproTeams30TelegramBot.storage.interfaces.BotStatesCache;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class BotStatesCacheImpl implements BotStatesCache {

    private final Map<Long, BotStates> botStates = new HashMap<>();

    @Override
    public void setState(Long userId, BotStates state) {
        botStates.put(userId, state);
    }

    @Override
    public BotStates getBotState(Long userId) {
        return botStates.getOrDefault(userId, null);
    }

    @Override
    public boolean containsStatus(Long userId) {
        return botStates.containsKey(userId);
    }
}
