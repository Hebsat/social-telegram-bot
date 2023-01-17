package com.example.javaproTeams30TelegramBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageTypingWsDto {
    @JsonProperty("user_id")
    private Long userId;
    private Long dialogId;
    private Boolean typing;
}
