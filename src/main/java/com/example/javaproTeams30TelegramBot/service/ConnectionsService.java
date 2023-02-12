package com.example.javaproTeams30TelegramBot.service;

import com.example.javaproTeams30TelegramBot.dto.CommonDto;
import com.example.javaproTeams30TelegramBot.dto.CurrencyDto;
import com.example.javaproTeams30TelegramBot.dto.PersonDto;
import com.example.javaproTeams30TelegramBot.dto.WeatherDto;
import com.example.javaproTeams30TelegramBot.util.mappers.PersonMapper;
import com.example.javaproTeams30TelegramBot.model.AuthStates;
import com.example.javaproTeams30TelegramBot.model.DataContainer;
import com.example.javaproTeams30TelegramBot.model.OtherStates;
import com.example.javaproTeams30TelegramBot.storage.interfaces.*;
import com.example.javaproTeams30TelegramBot.util.deserializers.CurrenciesDeserializer;
import com.example.javaproTeams30TelegramBot.util.deserializers.PersonDeserializer;
import com.example.javaproTeams30TelegramBot.util.deserializers.WeatherDeserializer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionsService {

    @Value("${server-url}")
    private String url;
    @Value("${bot.pagination.friend-size}")
    private int friendPageSize;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(PersonDto.class, new PersonDeserializer())
            .registerTypeAdapter(WeatherDto.class, new WeatherDeserializer())
            .registerTypeAdapter(CurrencyDto.class, new CurrenciesDeserializer())
            .create();

    private final AuthStatesCache authStatesCache;
    private final OtherStatesCache otherStatesCache;
    private final SettingsStatesCache settingsStatesCache;
    private final UserDialogsDataCache userDialogsDataCache;
    private final UserTokenCache userTokenCache;

    protected String authorizePerson(Long userId, String email, String password) {
        Type personType = new TypeToken<CommonDto<PersonDto>>(){}.getType();
        String answer = "";
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(url + "/api/v1/auth/login");
            JsonObject login = new JsonObject();
            login.addProperty("email", email);
            login.addProperty("password", password);
            StringEntity params = new StringEntity(login.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                authStatesCache.setAuthState(userId, AuthStates.UNAUTHORIZED);
                return "You entered incorrect data. Authorization failed!";
            }
            String jsonData = EntityUtils.toString(response.getEntity());
            CommonDto<PersonDto> person = gson.fromJson(jsonData, personType);
            userTokenCache.storeToken(userId, person.getData().getToken());
            answer = "You authorized as " + person.getData().getFirstName() + "! Good job!";
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
        authStatesCache.setAuthState(userId, AuthStates.AUTHORIZED);
        return answer;
    }

    protected PersonDto getCurrentPerson(Long userId) {
        Type personType = new TypeToken<CommonDto<PersonDto>>(){}.getType();
        String token = userTokenCache.getToken(userId);
        CommonDto<PersonDto> person = new CommonDto<>();
        if (token == null) {
            return null;
        }
        try {
            URLConnection connection = new URL(url + "/api/v1/users/me").openConnection();
            connection.setRequestProperty("Authorization", token);
            String jsonData = new String(connection.getInputStream().readAllBytes());
            person = gson.fromJson(jsonData, personType);
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
        return person.getData();
    }

    protected List<String> getUsersFriendsPage(Long userId, Integer pageNumber) {
        Type listOfPersons = new TypeToken<CommonDto<List<PersonDto>>>(){}.getType();
        String token = userTokenCache.getToken(userId);
        if (token == null) {
            return List.of(Messages.UNAUTHORIZED_MESSAGE);
        }
        List<String> answer = new ArrayList<>();
        try {
            URLConnection connection = new URL(url + "/api/v1/friends?offset=" +
                    pageNumber + "&perPage=" + friendPageSize).openConnection();
            connection.setRequestProperty("Authorization", token);
            String jsonData = new String(connection.getInputStream().readAllBytes());
            CommonDto<List<PersonDto>> persons = gson.fromJson(jsonData, listOfPersons);
            persons.getData().forEach(personDto -> answer.add(PersonMapper.getPersonInfo(personDto)));
            int friendsShown = persons.getData().size() + pageNumber * friendPageSize;
            StringJoiner closeMessage = new StringJoiner("\n");
            closeMessage.add("You have totally " + persons.getTotal() + " friends.");
            if (friendsShown < persons.getTotal()) {
                userDialogsDataCache.storeUserDialogsData(userId, DataContainer.<Integer>builder().data(pageNumber + 1).build());
                otherStatesCache.setCurrentState(userId, OtherStates.FRIENDS_PAGINATION);
                closeMessage.add("You are shown " + friendsShown + " friends").add("To view next friends page send /next");
            }
            else {
                userDialogsDataCache.clearUserDialogsData(userId);
                otherStatesCache.removeUsersStates(userId);
            }
            answer.add(closeMessage.toString());
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
        return answer;
    }

    protected String setNotificationsStatus(Long userId, boolean value) {
        String token = userTokenCache.getToken(userId);
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPut request = new HttpPut(url + "/api/v1/account/telegram?telegramId=" + userId + "&value=" + value);
            request.addHeader("Authorization", token);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                settingsStatesCache.removeCurrentSettingsStates(userId);
                otherStatesCache.removeUsersStates(userId);
                return "Notifications status wasn't set. Repeat later";
            }
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
        settingsStatesCache.removeCurrentSettingsStates(userId);
        otherStatesCache.removeUsersStates(userId);
        return "Now you will " + (value ? "" : "not ") + "receive notifications";
    }
}
