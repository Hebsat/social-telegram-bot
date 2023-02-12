package com.example.javaproTeams30TelegramBot.util.mappers;

import com.example.javaproTeams30TelegramBot.dto.PersonDto;

import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

public class PersonMapper {

    private static final String TIME_PATTERN = "HH:mm:ss dd.MM.yyyy";
    private static final String BIRTHDAY_PATTERN = "dd.MM.yyyy";

    public static String getPersonInfo(PersonDto person) {
        StringJoiner response = new StringJoiner(System.lineSeparator());
        response.add("First name: " + person.getFirstName());
        if (person.getLastName() != null) {
            response.add("Last name: " + person.getLastName());
        }
        if (person.getBirthDate() != null) {
            response.add("Birthday: " + person.getBirthDate().format(DateTimeFormatter.ofPattern(BIRTHDAY_PATTERN)));
        }
        if (person.getCountry() != null) {
            response.add("Country: " + person.getCountry());
        }
        if (person.getCity() != null) {
            response.add("City: " + person.getCity());
        }
        if (person.getAbout() != null) {
            response.add("About: " + person.getAbout());
        }
        if (person.getLastOnlineTime() != null) {
            response.add("Last online time: " + person.getLastOnlineTime().format(DateTimeFormatter.ofPattern(TIME_PATTERN)));
        }
        return response.toString();
    }
}
