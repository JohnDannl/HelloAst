package com.closeli.jd5737.helloworld;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    class Instrument {
        void play() {
            System.out.println("play Instrument");
        }
    }
    class Piano extends Instrument {
        class Player {
            String name;
            int age;
        }
        Player player = new Player();
        @SerializedName("images")
        String[] strArray = new String[]{"one", "two"};
        List<String> strList = new ArrayList<String>();
        String name;
        int age;
        float price;

        @Override
        void play() {
            System.out.println("play Piano");
        }
    }
    @Test
    public void sayHello() {
        Piano piano = new Piano();
        piano.play();
        piano.age = 20;
        piano.price = 1008;
        piano.player.name ="Lily";
        piano.player.age = 18;
        piano.strList.add("First");
        piano.strList.add("Second");
        GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingStrategy(new FieldNamingStrategy() {
            @Override
            public String translateName(Field field) {
                if (field.getName().equals("strList")) {
                    return "imageList";
                } else {
                    return field.getName();
                }
            }
        });
        builder.serializeNulls();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        System.out.println(gson.toJson(piano));
    }

    @Test
    public void sayGoodBye() throws IOException{
        String str_piano = "{\"player\":{\"name\":\"Lily\",\"age\":18},\"strArray\":"
                + "[\"one\",\"two\"],\"strList\":[\"First\",\"Second\"],"
                + "\"name\":null,\"age\":20,\"price\":1008.0}";
        JsonReader reader = new JsonReader(new StringReader(str_piano));
        handleObject(reader);
    }

    @Test
    public void parseTree() {
        String str_piano = "{\"player\":{\"name\":\"Lily\",\"age\":18},\"strArray\":"
                + "[\"one\",\"two\"],\"strList\":[\"First\",\"Second\"],"
                + "\"name\":null,\"age\":20,\"price\":1008.0}";
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(str_piano);
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            System.out.println(obj.toString());
            JsonObject player = obj.getAsJsonObject("player");
            System.out.println("name:" + player.get("name").getAsString() + ",age:" + player.get("age").getAsString());
            JsonArray strArray = obj.getAsJsonArray("strArray");
            for (int i = 0; i < strArray.size(); i++) {
                System.out.println((i + 1) + ":" + strArray.get(i).getAsString());
            }
        }
    }
    /**
     * Handle an Object. Consume the first token which is BEGIN_OBJECT. Within
     * the Object there could be array or non array tokens. We write handler
     * methods for both. Note the peek() method. It is used to find out the type
     * of the next token without actually consuming it. If <code>reader.hasNext()</code>
     * is used,the END_OBJECT won't be reached, but endObject() should be called at the end.
     * @param reader
     * @throws IOException
     */
    private static void handleObject(JsonReader reader) throws IOException
    {
        reader.beginObject();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.BEGIN_ARRAY)) {
                handleArray(reader);
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                handleObject(reader);
            } else {
                handleNonArrayToken(reader, token);
            }
        }
        reader.endObject();
    }

    /**
     * Handle a json array. The first token would be JsonToken.BEGIN_ARRAY.
     * Arrays may contain objects or primitives.
     *
     * @param reader
     * @throws IOException
     */
    public static void handleArray(JsonReader reader) throws IOException
    {
        reader.beginArray();
        while (true) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.END_ARRAY)) {
                reader.endArray();
                break;
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                handleObject(reader);
            } else if (token.equals(JsonToken.END_OBJECT)) {
                reader.endObject();
            } else
                handleNonArrayToken(reader, token);
        }
    }

    /**
     * Handle non array non object tokens
     *
     * @param reader
     * @param token
     * @throws IOException
     */
    public static void handleNonArrayToken(JsonReader reader, JsonToken token) throws IOException
    {
        if (token.equals(JsonToken.NAME)) {
            System.out.println(reader.nextName());
        } else if (token.equals(JsonToken.STRING)) {
            //reader.nextString();
            System.out.println(reader.nextString());
        } else if (token.equals(JsonToken.NUMBER)) {
            //reader.nextDouble();
            System.out.println(reader.nextDouble());
        } else {
            reader.skipValue();
        }

    }

    @Test
    public void stringFormat() {
        System.out.println(String.format("%h",14));
    }
}
