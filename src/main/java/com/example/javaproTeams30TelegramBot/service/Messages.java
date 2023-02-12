package com.example.javaproTeams30TelegramBot.service;

public class Messages {

    public static final String HELP_MESSAGE = "JavaTeam30Bot is a training project.\nTo control you can sent these " +
            "commands:\n\nMain commands:\n/start - to start the dialog with bot\n/stop - to reset you dialog and " +
            "remove all data\n/auth - to start authorization dialog\n/help - to get this message\n\nAfter " +
            "authorization commands:\n/settings - to set bot properties\n/myself - to get personal data about " +
            "yourself\n/friends - to get page of your friends\n/gratzme - to get greetings message from bot)" +
            "\n\nOptional commands:\n/next - to get next page of current dialog's information\n/notification - to set" +
            " notifications properties\n/unknown - to set unknown property value";

    public static final String UNAUTHORIZED_MESSAGE = "You are unauthorized.\nTo authorize use /auth";
}
