package com.example.javaproTeams30TelegramBot.util.deserializers;

import com.example.javaproTeams30TelegramBot.dto.CurrencyDto;
import com.example.javaproTeams30TelegramBot.dto.PersonDto;
import com.example.javaproTeams30TelegramBot.dto.WeatherDto;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class PersonDeserializer implements JsonDeserializer<PersonDto> {

    @Override
    public PersonDto deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        PersonDto person = new PersonDto();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("id")) {
            person.setId(jsonObject.get("id").getAsLong());
        }
        if (jsonObject.has("email")) {
            person.setEmail(jsonObject.get("email").getAsString());
        }
        if (jsonObject.has("phone") && !jsonObject.get("phone").getAsString().isEmpty()) {
            person.setPhone(jsonObject.get("phone").getAsString());
        }
        if (jsonObject.has("photo")) {
            person.setPhoto(jsonObject.get("photo").getAsString());
        }
        if (jsonObject.has("about")) {
            person.setAbout(jsonObject.get("about").getAsString());
        }
        if (jsonObject.has("city")) {
            person.setCity(jsonObject.get("city").getAsString());
        }
        if (jsonObject.has("country")) {
            person.setCountry(jsonObject.get("country").getAsString());
        }
        if (jsonObject.has("token")) {
            person.setToken(jsonObject.get("token").getAsString());
        }
        if (jsonObject.has("weather")) {
            person.setWeather(jsonDeserializationContext.deserialize(jsonObject.get("weather"), WeatherDto.class));
        }
        if (jsonObject.has("currency")) {
            person.setCurrency(jsonDeserializationContext.deserialize(jsonObject.get("currency"), CurrencyDto.class));
        }
        if (jsonObject.has("online")) {
            person.setOnline(jsonObject.get("online").getAsBoolean());
        }
        if (jsonObject.has("first_name")) {
            person.setFirstName(jsonObject.get("first_name").getAsString());
        }
        if (jsonObject.has("last_name")) {
            person.setLastName(jsonObject.get("last_name").getAsString());
        }
        if (jsonObject.has("reg_date")) {
            person.setRegDate(LocalDateTime.parse(jsonObject.get("reg_date").getAsString()));
        }
        if (jsonObject.has("birth_date")) {
            person.setBirthDate(LocalDateTime.parse(jsonObject.get("birth_date").getAsString()));
        }
        if (jsonObject.has("messages_permission")) {
            person.setMessagePermission(jsonObject.get("messages_permission").getAsString());
        }
        if (jsonObject.has("last_online_time")) {
            person.setLastOnlineTime(LocalDateTime.parse(jsonObject.get("last_online_time").getAsString()));
        }
        if (jsonObject.has("is_blocked")) {
            person.setIsBlocked(jsonObject.get("is_blocked").getAsBoolean());
        }
        if (jsonObject.has("is_blocked_by_current_user")) {
            person.setIsBlockedByCurrentUser(jsonObject.get("is_blocked_by_current_user").getAsBoolean());
        }
        if (jsonObject.has("friend_status")) {
            person.setFriendStatus(jsonObject.get("friend_status").getAsString());
        }
        if (jsonObject.has("user_deleted")) {
            person.setIsDeleted(jsonObject.get("user_deleted").getAsBoolean());
        }
        return person;
    }
}
