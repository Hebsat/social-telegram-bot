package com.example.javaproTeams30TelegramBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonDto {

    private Long id;

    private String email;

    private String phone;

    @JsonProperty(defaultValue = "photo")
    private String photo;

    private String about;

    private String city;

    private String country;

    private Boolean online;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("reg_date")
    private LocalDateTime regDate;

    @JsonProperty("birth_date")
    private LocalDateTime birthDate;

    @JsonProperty("messages_permission")
    private String messagePermission;

    @JsonProperty("last_online_time")
    private LocalDateTime lastOnlineTime;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    @JsonProperty("is_blocked_by_current_user")
    private Boolean isBlockedByCurrentUser;

    @JsonProperty("friend_status")
    private String friendStatus;

    @JsonProperty("user_deleted")
    private Boolean isDeleted;
}
