package com.example.javaproTeams30TelegramBot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramBotsService telegramBotsService;

    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String messageText = update.getMessage().getText();
        log.info("Received message by " + update.getMessage().getFrom().getUserName() + ": " + messageText);
        long userId = update.getMessage().getFrom().getId();
        switch (messageText) {
            case "/start": {
                sendMessage(telegramBotsService.startMessageReceived(
                        userId, update.getMessage().getChat().getFirstName()));
                break;
            }
            case "/auth": {
                sendMessage(telegramBotsService.authMessageReceived(userId));
                break;
            }
            case "/stop": {
                sendMessage(telegramBotsService.stopMessageReceived(userId));
                break;
            }
            case "/help": {
                sendMessage(telegramBotsService.helpMessageReceived(userId));
                break;
            }
            default: telegramBotsService.handleCurrentAuthState(userId, messageText).forEach(this::sendMessage);
        }
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
