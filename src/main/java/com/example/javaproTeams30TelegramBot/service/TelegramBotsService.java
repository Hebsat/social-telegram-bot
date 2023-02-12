package com.example.javaproTeams30TelegramBot.service;

import com.example.javaproTeams30TelegramBot.dto.PersonDto;
import com.example.javaproTeams30TelegramBot.util.mappers.PersonMapper;
import com.example.javaproTeams30TelegramBot.model.AuthStates;
import com.example.javaproTeams30TelegramBot.model.OtherStates;
import com.example.javaproTeams30TelegramBot.model.SettingsStates;
import com.example.javaproTeams30TelegramBot.storage.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotsService {

    private final AuthStatesCache authStatesCache;
    private final OtherStatesCache otherStatesCache;
    private final SettingsStatesCache settingsStatesCache;
    private final UserAuthDataCache userAuthDataCache;
    private final UserDialogsDataCache userDialogsDataCache;
    private final UserTokenCache userTokenCache;
    private final ConnectionsService connectionsService;

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
        return getSendMessage(userId, Messages.HELP_MESSAGE);
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
                return List.of(getSendMessage(userId, connectionsService.authorizePerson(userId, email, message)));
            }
            default: return handleCurrentMessage(userId, message);
        }
    }

    private List<SendMessage> handleCurrentMessage(Long userId, String message) {
        switch (message) {
            case "/settings": return handleSettingsCommand(userId);
            case "/myself": return getInfoAboutMyself(userId);
            case "/friends": return connectionsService.getUsersFriendsPage(userId, 0)
                    .stream().map(s -> getSendMessage(userId, s)).collect(Collectors.toList());
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
                    return List.of(getSendMessage(userId, connectionsService.setNotificationsStatus(userId, true)));

                } else if (message.equals("no")) {
                    return List.of(getSendMessage(userId, connectionsService.setNotificationsStatus(userId, false)));
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
                return connectionsService
                        .getUsersFriendsPage(userId, (Integer) userDialogsDataCache.getUserDialogsData(userId).getData())
                        .stream().map(s -> getSendMessage(userId, s)).collect(Collectors.toList());
            case POSTS_PAGINATION: return List.of(getSendMessage(userId, answer));
            case COMMENTS_PAGINATION: return List.of(getSendMessage(userId, answer));
            default: return List.of(getSendMessage(userId, answer));
        }
    }

    private List<SendMessage> handleSettingsCommand(Long userId) {
        if (!authStatesCache.getAuthState(userId).equals(AuthStates.AUTHORIZED)) {
            return List.of(getSendMessage(userId, Messages.UNAUTHORIZED_MESSAGE));
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

    private List<SendMessage> getInfoAboutMyself(Long userId) {
        PersonDto person = connectionsService.getCurrentPerson(userId);
        if (person == null) {
            return List.of(getSendMessage(userId, Messages.UNAUTHORIZED_MESSAGE));
        }
        return List.of(getSendMessage(userId, PersonMapper.getPersonInfo(person)));
    }

    private List<SendMessage> getGreetingsMessage(Long userId) {
        PersonDto person = connectionsService.getCurrentPerson(userId);
        if (person == null) {
            return List.of(getSendMessage(userId, Messages.UNAUTHORIZED_MESSAGE));
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
