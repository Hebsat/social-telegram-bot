package com.example.javaproTeams30TelegramBot.dto;

import lombok.Data;

@Data
public class CommonDto<T> {

    private Long timestamp = System.currentTimeMillis();

    private Integer offset;

    private Integer perPage;

    private Integer itemPerPage;

    private Long total;

    private T data;
}
