package com.example.javaproTeams30TelegramBot.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataContainer<T> {

    private T data;
}
