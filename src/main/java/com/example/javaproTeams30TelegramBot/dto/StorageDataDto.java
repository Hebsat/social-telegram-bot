package com.example.javaproTeams30TelegramBot.dto;

import lombok.Data;

@Data
public class StorageDataDto {

    private String id;

    private long ownerId;

    private String fileName;

    private String relativeFilePath;

    private String fileFormat;

    private long bytes;

    private String fileType;

    private long createdAt;
}
