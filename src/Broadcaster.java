
import Models.Constants;
import Models.Map;
import Models.Updates;
import TypeAdapter.UpdatesTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Broadcaster extends Thread implements Constants {
    private List<Session> sessions;
    private Updates updates;

    Broadcaster(List<Session> sessions, Updates updates) {
        this.sessions = sessions;
        this.updates = updates;
    }

    public void sendKill(String from, String to) {
        JsonObject jsonObject = new JsonObject();
        JsonObject body = new JsonObject();
        body.add("from", new JsonPrimitive(from));
        body.add("to", new JsonPrimitive(to));
        jsonObject.add("type", new JsonPrimitive("Kill"));
        jsonObject.add("body", body);
        broadcast(jsonObject.toString());
    }

    public void sendReject() {
        JsonObject jsonObject = new JsonObject();
        JsonObject body = new JsonObject();
        body.add("warning", new JsonPrimitive("name already used"));
        jsonObject.add("type", new JsonPrimitive("Reject"));
        jsonObject.add("body", body);
        broadcast(jsonObject.toString());
    }

    public void sendHit(String from, String to) {
        JsonObject jsonObject = new JsonObject();
        JsonObject body = new JsonObject();
        body.add("from", new JsonPrimitive(from));
        body.add("to", new JsonPrimitive(to));
        body.add("damage", new JsonPrimitive(Constants.DAMAGE));
        jsonObject.add("type", new JsonPrimitive("Hit"));
        jsonObject.add("body", body);
        broadcast(jsonObject.toString());
    }

    public void sendUpdates() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Updates.class, new UpdatesTypeAdapter())
                .create();
        broadcast(gson.toJson(updates));
    }

    private void broadcast(String message) {
        for (Session session : sessions) {
            OutputStream os = null;
            try {
                os = session.getOutputStream();
                byte[] bytes = message.getBytes();
                ByteBuffer buffer = ByteBuffer.allocate(2);
                buffer.putShort((short) bytes.length);
//                System.out.println("buffer.array : " + Arrays.toString(buffer.array()));
//                System.out.println("message : " + message);

                os.write(buffer.array());
                os.write(message.getBytes());
//                System.out.println("message: " + message);
            } catch (SocketException s) {
                sessions.remove(session);
                s.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
//                applyItems();
                sendUpdates();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void update(List<Session> sessions) {
        this.sessions = sessions;
    }
}
