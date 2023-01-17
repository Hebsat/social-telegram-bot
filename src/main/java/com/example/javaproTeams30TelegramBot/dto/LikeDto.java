package com.example.javaproTeams30TelegramBot.dto;

import lombok.Data;

import java.util.List;

@Data
public class LikeDto {

    private Integer likes;

    private List<Long> users;
}
