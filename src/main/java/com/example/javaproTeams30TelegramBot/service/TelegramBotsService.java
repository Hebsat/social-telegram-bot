package com.example.javaproTeams30TelegramBot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotsService {

    @Value("${server-url}")
    private String url;
    @Value("${token}")
    private String token;

    public String getUserInfo() {
        StringJoiner response = new StringJoiner(System.lineSeparator());
        try {
            URLConnection connection = new URL(url + "/api/v1/users/me").openConnection();
            connection.setRequestProperty("Authorization", token);
            String jsonData = new String(connection.getInputStream().readAllBytes());
            JSONObject person = new JSONObject(jsonData).getJSONObject("data");
            if (person.has("first_name")) {
                response.add("First name: " + person.get("first_name"));
            }
            if (person.has("last_name")) {
                response.add("Last name: " + person.get("last_name"));
            }
            if (person.has("birth_date")) {
                response.add("Birthday: " + person.get("birth_date"));
            }
            if (person.has("country")) {
                response.add("Country: " + person.get("country"));
            }
            if (person.has("city")) {
                response.add("City: " + person.get("city"));
            }
            if (person.has("about")) {
                response.add("About: " + person.get("about"));
            }
            if (person.has("last_online_time")) {
                response.add("Last online time: " + person.get("last_online_time"));
            }
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
        return response.toString();
    }
}
