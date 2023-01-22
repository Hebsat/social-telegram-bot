package com.example.javaproTeams30TelegramBot.util.deserializers;

import com.example.javaproTeams30TelegramBot.dto.CurrencyDto;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CurrenciesDeserializer implements JsonDeserializer<CurrencyDto> {

    @Override
    public CurrencyDto deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        CurrencyDto currency = new CurrencyDto();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (!jsonObject.isEmpty()) {
            currency.setUsd(jsonObject.get("usd").getAsDouble());
            currency.setEuro(jsonObject.get("euro").getAsDouble());
        }
        return currency;
    }
}
