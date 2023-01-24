package com.example.javaproTeams30TelegramBot.service;

import com.example.javaproTeams30TelegramBot.dto.CommonDto;
import com.example.javaproTeams30TelegramBot.dto.CurrencyDto;
import com.example.javaproTeams30TelegramBot.dto.PersonDto;
import com.example.javaproTeams30TelegramBot.dto.WeatherDto;
import com.example.javaproTeams30TelegramBot.mappers.PersonMapper;
import com.example.javaproTeams30TelegramBot.model.AuthStates;
import com.example.javaproTeams30TelegramBot.model.DataContainer;
import com.example.javaproTeams30TelegramBot.model.OtherStates;
import com.example.javaproTeams30TelegramBot.storage.interfaces.*;
import com.example.javaproTeams30TelegramBot.util.PaginationAdapter;
import com.example.javaproTeams30TelegramBot.util.deserializers.CurrenciesDeserializer;
import com.example.javaproTeams30TelegramBot.util.deserializers.PersonDeserializer;
import com.example.javaproTeams30TelegramBot.util.deserializers.WeatherDeserializer;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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
public class TelegramBotsService {

    @Value("${server-url}")
    private String url;
    @Value("${bot.pagination.friend-size}")
    private int friendPageSize;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(PersonDto.class, new PersonDeserializer())
            .registerTypeAdapter(WeatherDto.class, new WeatherDeserializer())
            .registerTypeAdapter(CurrencyDto.class, new CurrenciesDeserializer())
            .create();
    private static final String HELP_MESSAGE = "JavaTeam30Bot is a training project.\nTo control you can sent these commands:\n\n" +
            "Main commands:\n\n/start - to start the dialog with bot\n/stop - to reset you dialog and remove all data\n/auth - " +
            "to start authorization dialog\n/help - to get this message\n\nAfter authorization commands:\n/myself - to get personal" +
            " data about yourself\n/friends - to get page of your friends\n\nOptional commands:\n/next - to get next page of " +
            "current dialog's information";
    private static final String UNAUTHORIZED_MESSAGE = "You are unauthorized.\nTo authorize use /auth";

    private final AuthStatesCache authStatesCache;
    private final OtherStatesCache otherStatesCache;
    private final UserAuthDataCache userAuthDataCache;
    private final UserDialogsDataCache userDialogsDataCache;
    private final UserTokenCache userTokenCache;

    public SendMessage startMessageReceived(long userId, String name) {
        String answer = "Greetings you, " + name + "! Welcome to JavaTeam30Bot!\nAuthorize to get more " +
                "functions (/auth)\n/help for more information";
        if (!authStatesCache.containsAuthState(userId)) {
            authStatesCache.setAuthState(userId, AuthStates.UNAUTHORIZED);
        }
        return getSendMessage(userId, answer);
    }

    public SendMessage stopMessageReceived(long userId) {
        userAuthDataCache.clearUserAuthData(userId);
        authStatesCache.setAuthState(userId, AuthStates.UNAUTHORIZED);
        userTokenCache.removeToken(userId);
        String answer = "Authenticate data removed";
        return getSendMessage(userId, answer);
    }

    public SendMessage authMessageReceived(long userId) {
        String answer = "Enter your email:";
        authStatesCache.setAuthState(userId, AuthStates.ASK_LOGIN);
        return getSendMessage(userId, answer);
    }

    public SendMessage helpMessageReceived(long userId) {
        return getSendMessage(userId, HELP_MESSAGE);
    }

    public List<SendMessage> handleCurrentAuthState(Long userId, String message) {
        AuthStates currentAuthState = authStatesCache.getAuthState(userId);
        switch (currentAuthState) {
            case ASK_LOGIN: {
                userAuthDataCache.storeUserAuthData(userId, message);
                authStatesCache.setAuthState(userId, AuthStates.ASK_PASSWORD);
                return List.of(getSendMessage(userId,"Enter your password:"));
            }
            case ASK_PASSWORD: {
                String email = userAuthDataCache.getUserAuthData(userId);
                userAuthDataCache.clearUserAuthData(userId);
                return List.of(getSendMessage(userId, authorizePerson(userId, email, message)));
            }
            default: return handleCurrentMessage(userId, message);
        }
    }

    private List<SendMessage> handleCurrentMessage(Long userId, String message) {
        switch (message) {
            case "/myself": return getInfoAboutMyself(userId);
            case "/friends": return getUsersFriendsPage(userId, 0);
            case "/next": return handleNextCommand(userId);
            default: return handleNoneCommandMessage(userId, message);
        }
    }

    private List<SendMessage> handleNoneCommandMessage(Long userId, String message) {
        String answer = "I'm sorry, but i'm don't understand you! Maybe you are not authorized, your token is invalid" +
                " or you send an incorrect command. Use /help to get more information.";
        if (message.startsWith("Do something")) {
            return  List.of(getSendMessage(userId, "Something done!"));
        }
        if (message.startsWith("Hi ") || message.startsWith("Hello")) {
            return  List.of(getSendMessage(userId, "Hi, my darling:)"));
        }
        return List.of(getSendMessage(userId, answer));
    }

    private List<SendMessage> handleNextCommand(Long userId) {
        String answer = "I don't know what kind of information you want to get next time!";
        OtherStates state = otherStatesCache.getCurrentState(userId);
        switch (state) {
            case FRIENDS_PAGINATION:
                return getUsersFriendsPage(userId, (Integer) userDialogsDataCache.getUserDialogsData(userId).getData());
            case POSTS_PAGINATION: return List.of(getSendMessage(userId, answer));
            case COMMENTS_PAGINATION: return List.of(getSendMessage(userId, answer));
            default: return List.of(getSendMessage(userId, answer));
        }
    }

    private String authorizePerson(Long userId, String email, String password) {
        Type personType = new TypeToken<CommonDto<PersonDto>>(){}.getType();
        String answer = "";
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(url + "/api/v1/auth/login?telegramId=" + userId);
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

    private List<SendMessage> getInfoAboutMyself(Long userId) {
        Type personType = new TypeToken<CommonDto<PersonDto>>(){}.getType();
        String token = userTokenCache.getToken(userId);
        CommonDto<PersonDto> person = new CommonDto<>();
        if (token == null) {
            return List.of(getSendMessage(userId, UNAUTHORIZED_MESSAGE));
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
        return List.of(getSendMessage(userId,PersonMapper.getPersonInfo(person.getData())));
    }

    private List<SendMessage> getUsersFriendsPage(Long userId, Integer pageNumber) {
        Type listOfPersons = new TypeToken<CommonDto<List<PersonDto>>>(){}.getType();
        String token = userTokenCache.getToken(userId);
        if (token == null) {
            return List.of(getSendMessage(userId, UNAUTHORIZED_MESSAGE));
        }
        List<SendMessage> answer = new ArrayList<>();
        try {
            URLConnection connection = new URL(url + "/api/v1/friends?offset=" +
                    PaginationAdapter.getOffset(pageNumber, friendPageSize) + "&perPage=" + friendPageSize).openConnection();
            connection.setRequestProperty("Authorization", token);
            String jsonData = new String(connection.getInputStream().readAllBytes());
            CommonDto<List<PersonDto>> persons = gson.fromJson(jsonData, listOfPersons);
            persons.getData().forEach(personDto -> answer.add(getSendMessage(userId, PersonMapper.getPersonInfo(personDto))));
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
            answer.add(getSendMessage(userId, closeMessage.toString()));
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
        return answer;
    }

    private SendMessage getSendMessage(Long userId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(message);
        return sendMessage;
    }
}
