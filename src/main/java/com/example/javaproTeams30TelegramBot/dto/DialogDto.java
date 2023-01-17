package com.example.javaproTeams30TelegramBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DialogDto {

    private Long id;

    @JsonProperty("unread_count")
    private Long unreadCount;

    @JsonProperty("last_message")
    private MessageDto lastMessage;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("recipient_id")
    private Long recipientId;

    @JsonProperty("read_status")
    private String readStatus;
}
