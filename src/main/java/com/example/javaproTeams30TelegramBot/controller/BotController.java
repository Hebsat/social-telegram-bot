package com.example.javaproTeams30TelegramBot.controller;

import com.example.javaproTeams30TelegramBot.dto.NotificationDto;
import com.example.javaproTeams30TelegramBot.service.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@RestController
@RequiredArgsConstructor
public class BotController {

    private final TelegramBot telegramBot;

    @PostMapping("/bot")
    public ResponseEntity<?> sendNotification(@RequestBody NotificationDto notification, @RequestParam long userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(getMessageText(notification));
        telegramBot.sendMessage(sendMessage);
        return ResponseEntity.ok().build();
    }

    private String getMessageText(NotificationDto notification) {
        String userName = notification.getEntityAuthor().getFirstName() +
                (notification.getEntityAuthor().getLastName() == null ?
                        "" : " " + notification.getEntityAuthor().getLastName());
        switch (notification.getNotificationType()) {
            case "POST": return "Your friend " + userName + " create new post: \"" + notification.getInfo() + "\"!";
            case "POST_COMMENT":
            case "COMMENT_COMMENT": return "User " + userName + " commented your " + notification.getInfo() + "!";
            case "FRIEND_REQUEST": return "User " + userName + " wants to be your fiend!";
            case "MESSAGE": return "User " + userName + " send message to you: \"" + notification.getInfo() + "\"!";
            case "FRIEND_BIRTHDAY": return "Your friend " + userName + " has a birthday today! He(She) is " + notification.getInfo() + "!";
            case "POST_LIKE": return "User " + userName + " liked your " + notification.getInfo() + "!";
            default: return "Something happened!";
        }
    }
}
