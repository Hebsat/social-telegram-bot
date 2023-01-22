package com.example.javaproTeams30TelegramBot.util.deserializers;

import com.example.javaproTeams30TelegramBot.dto.WeatherDto;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class WeatherDeserializer implements JsonDeserializer<WeatherDto> {

    @Override
    public WeatherDto deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        WeatherDto weather = new WeatherDto();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (!jsonObject.isEmpty()) {
            weather.setCity(jsonObject.get("city").getAsString());
            weather.setDate(LocalDateTime.parse(jsonObject.get("date").getAsString()));
            weather.setDescription(jsonObject.get("clouds").getAsString());
            weather.setTemp(jsonObject.get("temp").getAsDouble());
        }
        return weather;
    }
}
