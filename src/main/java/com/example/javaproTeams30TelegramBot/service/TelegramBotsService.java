package com.example.javaproTeams30TelegramBot.service;

import com.example.javaproTeams30TelegramBot.model.BotStates;
import com.example.javaproTeams30TelegramBot.storage.interfaces.BotStatesCache;
import com.example.javaproTeams30TelegramBot.storage.interfaces.UserDataCache;
import com.example.javaproTeams30TelegramBot.storage.interfaces.UserTokenCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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

    private final BotStatesCache botStatesCache;
    private final UserDataCache userDataCache;
    private final UserTokenCache userTokenCache;

    public SendMessage startMessageReceived(long userId, String name) {
        String answer = "Greetings you, " + name + "! Welcome to Team30TelegramBot!\nAuthorize please (/auth)\n/help for more information";
        if (!botStatesCache.containsStatus(userId)) {
            botStatesCache.setState(userId, BotStates.UNAUTHORIZED);
        }
        return getSendMessage(userId, answer);
    }

    public SendMessage stopMessageReceived(long userId) {
        userDataCache.clearUserData(userId);
        botStatesCache.setState(userId, BotStates.UNAUTHORIZED);
        String answer = "Authenticate data removed";
        return getSendMessage(userId, answer);
    }

    public SendMessage authMessageReceived(long userId) {
        String answer = "Enter your email:";
        botStatesCache.setState(userId, BotStates.ASK_LOGIN);
        return getSendMessage(userId, answer);
    }

    public SendMessage helpMessageReceived(long userId) {
        String answer = "In development";
        return getSendMessage(userId, answer);
    }

    public SendMessage handleCurrentState(Long userId, String message) {
        String answer;
        BotStates currentState = botStatesCache.getBotState(userId);
        switch (currentState) {
            case ASK_LOGIN: {
                userDataCache.storeUserData(userId, message);
                botStatesCache.setState(userId, BotStates.ASK_PASSWORD);
                answer = "Enter your password:";
                break;
            }
            case ASK_PASSWORD: {
                String email = userDataCache.getUserData(userId);
                answer = authorizePerson(userId, email, message);
                break;
            }
            default: answer = handleCurrentMessage(userId, message);
        }
        return getSendMessage(userId, answer);
    }

    private String handleCurrentMessage(Long userId, String message) {
        String answer = "I'm sorry, but i'm don't understand you!";
        if (message.startsWith("do something")) {
            return  "done something";
        }
        return answer;
    }

    private String authorizePerson(Long userId, String email, String password) {
        String answer = "";
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(url + "/api/v1/auth/login");
            JSONObject login = new JSONObject();
            login.put("email", email);
            login.put("password", password);
            StringEntity params = new StringEntity(login.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                botStatesCache.setState(userId, BotStates.UNAUTHORIZED);
                return "You entered incorrect data. Authorization failed!";
            }
            String jsonData = EntityUtils.toString(response.getEntity());
            JSONObject person = new JSONObject(jsonData).getJSONObject("data");
            String token = person.getString("token");
            String name = person.getString("first_name") + " " + person.get("last_name");
            userTokenCache.storeToken(userId, token);
            answer = "You authorized as " + name + "! Good job!";
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
        botStatesCache.setState(userId, BotStates.AUTHORIZED);
        return answer;
    }

    public SendMessage myselfMessageReceived(Long userId) {
        String token = userTokenCache.getToken(userId);
        if (token == null) {
            return getSendMessage(userId,"unauthorized person");
        }
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
        return getSendMessage(userId,response.toString());
    }

    private SendMessage getSendMessage(Long userId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(message);
        return sendMessage;
    }
}
