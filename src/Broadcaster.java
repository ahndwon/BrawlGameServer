
import Models.Constants;
import Models.Map;
import Models.Updates;
import TypeAdapter.MapTypeAdapter;
import TypeAdapter.UpdatesTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;

public class Broadcaster extends Thread implements Constants {
    private List<Session> sessions;
    private Updates updates;
    private HashMap<Integer, Integer> itemRespawn;
    private Map map;


    Broadcaster(List<Session> sessions, Updates updates, Map map) {
        this.sessions = sessions;
        this.updates = updates;
        itemRespawn = new HashMap<>();
        this.map = map;
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
            } catch (SocketException ignore) {
                updates.getUpdates().remove(session.getUser().getName());

                sessions.remove(session);
//                s.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void run() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendUpdates();

                List<Integer> itemIndex = new ArrayList<>(itemRespawn.keySet());

                for (Integer index : itemIndex) {
                    System.out.println("index :" + index + ", " + itemRespawn.get(index));
                    itemRespawn.replace(index, itemRespawn.get(index) - 10);
                    if (itemRespawn.get(index) < 0) {
                        map.getMap()[index] = 2;
//                        sendMap();
                        sendCorrectMap(index, 2);
                        itemRespawn.remove(index);
                    }
                }
            }
        }, 0, 100);

    }

    void update(List<Session> sessions) {
        this.sessions = sessions;
    }

    public void addItemRespawn(int index) {
        itemRespawn.put(index, 1000);
    }

    public void sendMap() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Map.class, new MapTypeAdapter())
                .create();
        broadcast(gson.toJson(map));
    }

    public void sendCorrectMap(int index, int message) {
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "Correct");
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("message", message);
        broadcast(jsonObject.toString());
    }
}