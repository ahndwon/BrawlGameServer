package TypeAdapter;

import Models.Image;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ImageTypeAdapter extends TypeAdapter {
    @Override
    public void write(JsonWriter jsonWriter, Object o) throws IOException {

    }

    @Override
    public Object read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();

        Image image = new Image();
        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "body":
                    break;

                case "num":
                    image.setCharacterImage(jsonReader.nextInt());
                    break;
            }
        }
        jsonReader.endObject();

        return image;
    }
}
