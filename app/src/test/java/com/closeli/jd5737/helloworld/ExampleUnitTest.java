package com.closeli.jd5737.helloworld;

import android.graphics.drawable.Drawable;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
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

    @Test
    public void gsonTest() {
        String jsonStr = "[{\"strategyId\":\"1798679d4851431684e9257ac84f371d\",\"strategyName\":\"新增策略t2\",\"rules\":" +
                "[{\"valid\":1,\"conditons\":[{\"cParamId\":\"350312dea19e11e68b820050569d3255\",\"cRangeMaxValue\":\"1\",\"cDevi" +
                "ceId\":\"2547ce7567df40efb6bef08d2d1a5f94\",\"cRangeMinValue\":\"0\",\"cTrigValue\":\"1\"}],\"actions\":[{\"aPara" +
                "mValue\":\"1\",\"aDeviceId\":\"2547ce7567df40efb6bef08d2d1a5f94\",\"num\":0,\"execTime\":1,\"aParamId\":\"350312" +
                "dea19e11e68b820050569d3255\"}]}]},{\"strategyId\":\"49ea7e3f802c46268e577e68048732ed\",\"strategyName\":\"新增策" +
                "略t2\",\"rules\":[{\"valid\":1,\"conditons\":[{\"cParamId\":\"350312dea19e11e68b820050569d3255\",\"cRangeMaxVal" +
                "ue\":\"1\",\"cDeviceId\":\"2547ce7567df40efb6bef08d2d1a5f94\",\"cRangeMinValue\":\"0\",\"cTrigValue\":\"1\"}],\"ac" +
                "tions\":[{\"aParamValue\":\"1\",\"aDeviceId\":\"2547ce7567df40efb6bef08d2d1a5f94\",\"num\":0,\"execTime\":1,\"aPa" +
                "ramId\":\"350312dea19e11e68b820050569d3255\"}]}]},{\"strategyId\":\"682b79a19e6d4265a61c198b2c7db678\",\"strateg" +
                "yName\":\"新增策略t2\",\"rules\":[{\"valid\":1,\"conditons\":[{\"cParamId\":\"350312dea19e11e68b820050569" +
                "d3255\",\"cRangeMaxValue\":\"1\",\"cDeviceId\":\"2547ce7567df40efb6bef08d2d1a5f94\",\"cRangeMinValue\":\"0\",\"cTrig" +
                "Value\":\"1\"}],\"actions\":[{\"aParamValue\":\"1\",\"aDeviceId\":\"2547ce7567df40efb6bef08d2d1a5f94\",\"num\":0,\"exe" +
                "cTime\":1,\"aParamId\":\"350312dea19e11e68b820050569d3255\"}]}]},{\"strategyId\":\"edbae426e9fd4dff86b6182d2a" +
                "52e100\",\"strategyName\":\"新增策略t2\",\"rules\":[{\"valid\":2,\"conditons\":[{\"cParamId\":\"350312dea1" +
                "9e11e68b820050569d3255\",\"cRangeMaxValue\":\"\",\"cDeviceId\":\"2547ce7567df40efb6bef08d2d1a5f94\",\"cRan" +
                "geMinValue\":\"\",\"cTrigValue\":\"1\"}],\"actions\":[{\"aParamValue\":\"1\",\"aDeviceId\":\"2547ce7567df4" +
                "0efb6bef08d2d1a5f94\",\"num\":0,\"execTime\":1,\"aParamId\":\"350312dea19e11e68b820050569d3255\"}]}]}]";
        Gson gson = new Gson();
        List<StrategyInfo> infos = gson.fromJson(jsonStr, new TypeToken<List<StrategyInfo>>(){}.getType());
        for (StrategyInfo info : infos) {
            System.out.println(info.strategyName);
        }
    }
    class StrategyInfo {
        public static final int GROUP_TITLE = 1;
        public static final int SIMPLE = 2;
        public static final int DETAIL = 3;
        public static final int THUMBNAIL = 4;
        public static final int MIDDEL_CONTENT = 5;
        public static final int SWITCH = 6;
        public String strategyId;
        public String strategyName;
        public String strategyDescription;
        public String thumbUrl;
        public Drawable thumbnail;
        public int itemType;
        public StrategyInfo(Drawable thumb, int itemType) {
            this.thumbnail = thumb;
            this.itemType = itemType;
        }
        public StrategyInfo(String name, int itemType) {
            this.strategyName = name;
            this.itemType = itemType;
        }
        public StrategyInfo(String name, String desc, int itemType) {
            this.strategyName = name;
            this.strategyDescription = desc;
            this.itemType = itemType;
        }
    }
}
