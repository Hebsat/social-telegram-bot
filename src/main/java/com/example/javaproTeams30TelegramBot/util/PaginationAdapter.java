package com.example.javaproTeams30TelegramBot.util;

public class PaginationAdapter {

    public static int getOffset(int page, int size) {
        return page * size;
    }
}
