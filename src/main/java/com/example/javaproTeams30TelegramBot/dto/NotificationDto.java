package com.example.javaproTeams30TelegramBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationDto {

    private String info;

    @JsonProperty("type")
    private String notificationType;

    @JsonProperty("author")
    private PersonDto entityAuthor;
}
