package com.nullpointexecutioners.buzzfilms.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.nullpointexecutioners.buzzfilms.Movie;

import java.lang.reflect.Type;

public class MovieDeserializer implements JsonDeserializer<Movie> {

    @Override
    public Movie deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        return new Movie(jsonObject.get("title").getAsString(), jsonObject.get("release_date").getAsString(),
                jsonObject.get("overview").getAsString(), jsonObject.get("poster_path").getAsString(),
                jsonObject.get("vote_average").getAsFloat());
    }
}
