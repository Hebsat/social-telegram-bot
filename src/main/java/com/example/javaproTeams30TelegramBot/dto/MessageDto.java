package com.example.javaproTeams30TelegramBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class MessageDto {

    private Long id;

    private ZonedDateTime time;

    private Boolean isSentByMe;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("recipient_id")
    private Long recipientId;

    @JsonProperty("message_text")
    private String messageText;

    @JsonProperty("read_status")
    private String readStatus;

    private PersonDto recipient;
}
