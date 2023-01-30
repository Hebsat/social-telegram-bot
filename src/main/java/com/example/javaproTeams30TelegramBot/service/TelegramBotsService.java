package com.example.javaproTeams30TelegramBot.service;

import com.example.javaproTeams30TelegramBot.dto.CommonDto;
import com.example.javaproTeams30TelegramBot.dto.CurrencyDto;
import com.example.javaproTeams30TelegramBot.dto.PersonDto;
import com.example.javaproTeams30TelegramBot.dto.WeatherDto;
import com.example.javaproTeams30TelegramBot.mappers.PersonMapper;
import com.example.javaproTeams30TelegramBot.model.AuthStates;
import com.example.javaproTeams30TelegramBot.model.DataContainer;
import com.example.javaproTeams30TelegramBot.model.OtherStates;
import com.example.javaproTeams30TelegramBot.model.SettingsStates;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private static final String HELP_MESSAGE = "JavaTeam30Bot is a training project.\nTo control you can sent these " +
            "commands:\n\nMain commands:\n/start - to start the dialog with bot\n/stop - to reset you dialog and " +
            "remove all data\n/auth - to start authorization dialog\n/help - to get this message\n\nAfter " +
            "authorization commands:\n/settings - to set bot properties\n/myself - to get personal data about " +
            "yourself\n/friends - to get page of your friends\n/gratzme - to get greetings message from bot)" +
            "\n\nOptional commands:\n/next - to get next page of current dialog's information\n/notification - to set" +
            " notifications properties\n/unknown - to set unknown property value";
    private static final String UNAUTHORIZED_MESSAGE = "You are unauthorized.\nTo authorize use /auth";

    private final AuthStatesCache authStatesCache;
    private final OtherStatesCache otherStatesCache;
    private final SettingsStatesCache settingsStatesCache;
    private final UserAuthDataCache userAuthDataCache;
    private final UserDialogsDataCache userDialogsDataCache;
    private final UserTokenCache userTokenCache;

    public SendMessage startMessageReceived(long userId, String name) {
        String answer = "Greetings you, " + name + "! Welcome to JavaTeam30Bot!\nAuthorize to get more " +
                "functions (/auth)\n/help for more information";
        if (!authStatesCache.containsAuthState(userId)) {
            authStatesCache.setAuthState(userId, AuthStates.UNAUTHORIZED);
        }
        userAuthDataCache.clearUserAuthData(userId);
        userDialogsDataCache.clearUserDialogsData(userId);
        otherStatesCache.removeUsersStates(userId);
        return getSendMessage(userId, answer);
    }

    public SendMessage stopMessageReceived(long userId) {
        userAuthDataCache.clearUserAuthData(userId);
        userDialogsDataCache.clearUserDialogsData(userId);
        authStatesCache.setAuthState(userId, AuthStates.UNAUTHORIZED);
        userTokenCache.removeToken(userId);
        otherStatesCache.removeUsersStates(userId);
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
            case "/settings": return handleSettingsCommand(userId);
            case "/myself": return getInfoAboutMyself(userId);
            case "/friends": return getUsersFriendsPage(userId, 0);
            case "/gratzme": return getGreetingsMessage(userId);
            case "/next": return handleNextCommand(userId);
            case "/notification": return handleNotificationProperty(userId);
            case "/unknown": return handleUnknownProperty(userId);
            default: return handleNoneCommandMessage(userId, message);
        }
    }

    private List<SendMessage> handleNoneCommandMessage(Long userId, String message) {
        String answer = "I'm sorry, but i'm don't understand you! Maybe you are not authorized, your token is invalid" +
                " or you send an incorrect command. Use /help to get more information.";
        switch (otherStatesCache.getCurrentState(userId)) {
            case SETTINGS_DIALOG: return handleNoneCommandSettingsMessage(userId, message);
            default: break;
        }
        if (message.startsWith("Do something")) {
            return  List.of(getSendMessage(userId, "Something done!"));
        }
        if (message.startsWith("Hi ") || message.startsWith("Hello")) {
            return  List.of(getSendMessage(userId, "Hi, my darling:)"));
        }
        return List.of(getSendMessage(userId, answer));
    }

    private List<SendMessage> handleNoneCommandSettingsMessage(Long userId, String message) {
        String answer = "Please write do you want more correctly.";
        switch (settingsStatesCache.getCurrentSettingsState(userId)) {
            case UNKNOWN: {
                settingsStatesCache.removeCurrentSettingsStates(userId);
                otherStatesCache.removeUsersStates(userId);
                return List.of(getSendMessage(userId, "Maybe, this value has been set for some unknown property.."));
            }
            case NOTIFICATIONS: {
                if (message.equals("yes")) {
                    return setNotificationsStatus(userId, true);

                } else if (message.equals("no")) {
                    return setNotificationsStatus(userId, false);
                }
                else return List.of(getSendMessage(userId, answer));
            }
            default: return List.of(getSendMessage(userId, answer));
        }
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

    private List<SendMessage> handleSettingsCommand(Long userId) {
        if (!authStatesCache.getAuthState(userId).equals(AuthStates.AUTHORIZED)) {
            return List.of(getSendMessage(userId, UNAUTHORIZED_MESSAGE));
        }
        String answer = "Select property:\n/notification - to set notifications sending\n/unknown - to set unknown property";
        otherStatesCache.setCurrentState(userId, OtherStates.SETTINGS_DIALOG);
        return List.of(getSendMessage(userId, answer));
    }

    private List<SendMessage> handleNotificationProperty(Long userId) {
        if (!otherStatesCache.getCurrentState(userId).equals(OtherStates.SETTINGS_DIALOG)) {
            return List.of(getSendMessage(userId, "What do you want?"));
        }
        String answer = "Do you want to get notifications? (yes/no)";
        settingsStatesCache.setCurrentSettingsState(userId, SettingsStates.NOTIFICATIONS);
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("yes");
        yesButton.setCallbackData("yes");
        InlineKeyboardButton noButton = new InlineKeyboardButton();
        yesButton.setText("no");
        yesButton.setCallbackData("no");
        markupInLine.setKeyboard(List.of(List.of(yesButton, noButton)));
        return List.of(getSendMessage(userId, answer));
    }

    private List<SendMessage> handleUnknownProperty(Long userId) {
        if (!otherStatesCache.getCurrentState(userId).equals(OtherStates.SETTINGS_DIALOG)) {
            return List.of(getSendMessage(userId, "What do you want?"));
        }
        String answer = "Set value for unknown property:";
        settingsStatesCache.setCurrentSettingsState(userId, SettingsStates.UNKNOWN);
        return List.of(getSendMessage(userId, answer));
    }

    private String authorizePerson(Long userId, String email, String password) {
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

    private List<SendMessage> getInfoAboutMyself(Long userId) {
        PersonDto person = getCurrentPerson(userId);
        if (person == null) {
            return List.of(getSendMessage(userId, UNAUTHORIZED_MESSAGE));
        }
        return List.of(getSendMessage(userId,PersonMapper.getPersonInfo(person)));
    }

    private PersonDto getCurrentPerson(Long userId) {
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

    private List<SendMessage> getUsersFriendsPage(Long userId, Integer pageNumber) {
        Type listOfPersons = new TypeToken<CommonDto<List<PersonDto>>>(){}.getType();
        String token = userTokenCache.getToken(userId);
        if (token == null) {
            return List.of(getSendMessage(userId, UNAUTHORIZED_MESSAGE));
        }
        List<SendMessage> answer = new ArrayList<>();
        try {
            URLConnection connection = new URL(url + "/api/v1/friends?offset=" +
                    pageNumber + "&perPage=" + friendPageSize).openConnection();
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

    private List<SendMessage> setNotificationsStatus(Long userId, boolean value) {
        String token = userTokenCache.getToken(userId);
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPut request = new HttpPut(url + "/api/v1/account/telegram?telegramId=" + userId + "&value=" + value);
            request.addHeader("Authorization", token);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                settingsStatesCache.removeCurrentSettingsStates(userId);
                otherStatesCache.removeUsersStates(userId);
                return List.of(getSendMessage(userId, "Notifications status wasn't set. Repeat later"));
            }
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
        settingsStatesCache.removeCurrentSettingsStates(userId);
        otherStatesCache.removeUsersStates(userId);
        String answer = "Now you will " + (value ? "" : "not ") + "receive notifications";
        return List.of(getSendMessage(userId, answer));
    }

    private List<SendMessage> getGreetingsMessage(Long userId) {
        PersonDto person = getCurrentPerson(userId);
        if (person == null) {
            return List.of(getSendMessage(userId, UNAUTHORIZED_MESSAGE));
        }
        LocalDate currentTime = LocalDate.now(ZoneId.of("Europe/Moscow"));
        StringJoiner answer = new StringJoiner(System.lineSeparator());
        answer.add("JavaTeam30Bot greetings you, dear " + person.getFirstName() + "!")
                .add("Today is " + currentTime.getDayOfWeek().name() +  currentTime.format(DateTimeFormatter.ofPattern(", dd.MM.yyyy")))
                .add("The currencies values are:")
                .add("\t- USD: " + person.getCurrency().getUsd())
                .add("\t- EUR: " + person.getCurrency().getEuro());
        if (person.getWeather().getCity() != null) {
            answer.add("Weather in " + person.getWeather().getCity() + " are:")
                    .add("\tTemperature: " + person.getWeather().getTemp() + " °C")
                    .add("\t" + person.getWeather().getDescription());
        }
        answer.add("Have a nice day!");
        return List.of(getSendMessage(userId, answer.toString()));
    }

    private SendMessage getSendMessage(Long userId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(message);
        return sendMessage;
    }

    private SendMessage getSendMessage(Long userId, String message, InlineKeyboardMarkup buttons) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(buttons);
        return sendMessage;
    }
}
