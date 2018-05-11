package TypeAdapter;

import Models.Map;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

public class MapTypeAdapter extends TypeAdapter<Map> {

    @Override
    public void write(JsonWriter writer, Map map) throws IOException {
        writer.beginObject();
        writer.name("type").value(map.getClass().getSimpleName());
        writer.name("body").beginObject();
        writer.name("map");
        writer.beginArray();
        for (int i = 0; i < map.getMap().length; i++) {
            writer.value(map.getMap()[i]);
        }
        writer.endArray();
//        writer.name("map").value(Arrays.toString(map.getMap()));
//        writer.name("map").beginArray()
        writer.endObject();
        writer.endObject();
    }

    @Override
    public Map read(JsonReader reader) throws IOException {
        reader.beginArray();
//        reader.beginObject();

//        Map m = new Map();
//
//        while (reader.hasNext()) {
//            switch (reader.nextName()) {
//                case "name":
//                    c.setUser(reader.nextString());
//                    break;
//                case "character":
//                    c.setCharacter(reader.nextInt());
//                    break;
//                case "position":
//                    reader.beginObject();
//                    break;
//                case "x":
//                    c.getPosition().setX((float) reader.nextDouble());
//                    break;
//                case "y":
//                    c.getPosition().setY((float) reader.nextDouble());
//                    break;
//            }
//        }
//        reader.endObject();
//        reader.endObject();
//        return c;
//    }
        return new Map();
    }
}