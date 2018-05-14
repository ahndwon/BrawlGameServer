
import Models.Constants;
import Models.Map;
import Models.Update;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
            } catch (SocketException s) {
                sessions.remove(session);
                s.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
    }


    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                // TODO...
                sendUpdates();
//                int index = random.nextInt(words.size());


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(List<Session> sessions) {
        this.sessions = sessions;
    }

//    public void applyItems(Map map) {
//        int[] m = map.getMap();
//        List<String> updateList = new ArrayList<>(updates.getUpdates().keySet());
//        for (int i = 0; i < m.length; i++) {
//            for (String name :
//                    updateList) {
//                Update update = updates.getUpdates().get(name);
//
//                if (l.getIndexByPos((int) update.getX(), (int) update.getY()) == i) {
//                    switch (m[i]) {
//                        case 1:
//                            update.setSpeed(PLAYER_SPEEDSLOW);
//                            for (Session s : sessions) {
//                                if (update.getUser().equals(s.getUser().getName())) {
//                                    s.getUser().setSpeed(PLAYER_SPEEDSLOW);
//                                }
//                            }
//                            break;
//
//                        case 2:
//                            if( update.getHp() <= FULL_HP - HEAL)
//                                update.setHp(update.getHp() + HEAL);
//                            for (Session s : sessions) {
//                                if (update.getUser().equals(s.getUser().getName())) {
//                                    if( s.getUser().getHp() <= FULL_HP - HEAL)
//                                        s.getUser().setHp(s.getUser().getHp() + HEAL);
//                                }
//                            }
//                            break;
//                    }
//                } else {
//                    update.setSpeed(PLAYER_SPEED);
//                    for (Session s : sessions) {
//                        if (update.getUser().equals(s.getUser().getName())) {
//                            s.getUser().setSpeed(PLAYER_SPEED);
//                        }
//                    }
//                }
//            }
//        }
//
//    }
}
