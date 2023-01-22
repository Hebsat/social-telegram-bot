package com.example.javaproTeams30TelegramBot.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeatherDto {

    private String description;

    private Double temp;

    private String city;

    private LocalDateTime date;
}
