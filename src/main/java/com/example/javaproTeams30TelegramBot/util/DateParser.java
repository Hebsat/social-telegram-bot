package com.example.javaproTeams30TelegramBot.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateParser {

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm");

    public static LocalDateTime format(String date) {
        return LocalDateTime.parse(date, FORMATTER);
    }
}
