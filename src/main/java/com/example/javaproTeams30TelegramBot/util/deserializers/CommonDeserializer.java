package com.example.javaproTeams30TelegramBot.util.deserializers;

import com.example.javaproTeams30TelegramBot.dto.CommonDto;
import com.example.javaproTeams30TelegramBot.dto.PersonDto;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CommonDeserializer implements JsonDeserializer<CommonDto<PersonDto>> {
    @Override
    public CommonDto<PersonDto> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }
}
