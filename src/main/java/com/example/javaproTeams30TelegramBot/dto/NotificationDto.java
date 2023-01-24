package com.example.javaproTeams30TelegramBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {

    private long id;

    private String info;

    @JsonProperty("notification_type")
    private String notificationType;

    @JsonProperty("entity_author")
    private PersonDto entityAuthor;
}
