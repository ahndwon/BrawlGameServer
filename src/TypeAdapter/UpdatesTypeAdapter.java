package TypeAdapter;

import Models.Update;
import Models.Updates;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdatesTypeAdapter extends TypeAdapter<Updates> {
    @Override
    public void write(JsonWriter writer, Updates updates) throws IOException {
        writer.beginObject();
        writer.name("type").value("Update");
        writer.name("users").beginArray();
        List<String> updatesKeys = new ArrayList<>(updates.getUpdates().keySet());

        for (String key:updatesKeys) {
            Update update = updates.getUpdates().get(key);
            writer.beginObject();
            writer.name("user").value(update.getUser());
            writer.name("x").value(update.getX());
            writer.name("y").value(update.getY());
            writer.name("hp").value(update.getHp());
            writer.name("direction").value(update.getDirection());
            writer.name("score").value(update.getScore());
            writer.name("state").value(update.getState());
            writer.endObject();
        }
        writer.endArray();
        writer.endObject();
    }

    @Override
    public Updates read(JsonReader reader) throws IOException {

        reader.beginObject();

        Updates updates = new Updates();
//
//        while (reader.hasNext()) {
//            switch (reader.nextName()) {
//                case "users" :
//                    reader.beginArray();
//                    reader.beginObject();
//                    break;
//                case "user" :
//                    update.setUser(reader.nextString());
//                    break;
//                case "x" :
//                    update.setX((float) reader.nextDouble());
//                    break;
//                case "y":
//                    update.setY((float) reader.nextDouble());
//                    break;
//                case "hp":
//                    update.setHp(reader.nextInt());
//                    break;
//                case "direction":
//                    update.setDirection(reader.nextString());
//                    break;
//                case "score" :
//                    update.setScore(reader.nextInt());
//                    break;
//                case "state":
//                    update.setState(reader.nextString());
//                    break;
//            }
//        }
        return updates;
    }
}
