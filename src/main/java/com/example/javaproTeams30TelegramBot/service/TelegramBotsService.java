package com.example.javaproTeams30TelegramBot.service;

import com.example.javaproTeams30TelegramBot.dto.PersonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class TelegramBotsService {

    public String getUserInfo(String email) {
        PersonDto person = null;
        if (person == null) {
            return "Person not found!";
        }
        StringJoiner response = new StringJoiner(System.lineSeparator());
        response.add("First name " + person.getFirstName())
                .add("Last name " + person.getLastName())
                .add("Birthday " + person.getBirthDate().toString())
                .add("Country " + person.getCountry())
                .add("City " + person.getCity())
                ;
        return response.toString();
    }
}
