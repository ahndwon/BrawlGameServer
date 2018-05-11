package TypeAdapter;

import Models.Move;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class MoveTypeAdapter extends TypeAdapter<Move> {

    @Override
    public void write(JsonWriter writer, Move move) throws IOException {
        writer.beginObject();
        writer.name("type").value(move.getClass().getSimpleName());
        writer.name("body").beginObject();
        writer.name("direction").value(move.getDirection());
        writer.endObject();
        writer.endObject();
    }

    @Override
    public Move read(JsonReader reader) throws IOException {
        reader.beginObject();

        Move m = new Move();

        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "body":
                    reader.beginObject();
                    break;
                case "direction":
                    m.setDirection(reader.nextString());
                    break;
            }
        }
        reader.endObject();
        return m;
    }
}
