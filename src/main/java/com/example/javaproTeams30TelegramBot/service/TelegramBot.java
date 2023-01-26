package com.example.javaproTeams30TelegramBot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    {
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", " - to start dialog"));
        commandList.add(new BotCommand("/stop", " - to remove all data and stop dialogs"));
        commandList.add(new BotCommand("/auth", " - to authorize"));
        commandList.add(new BotCommand("/help", " - to read help"));
        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e) {
            log.error("Error initialization commands menu: " + e.getMessage());
        }
    }

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

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
