
import Models.*;
import Models.Map;
import TypeAdapter.MapTypeAdapter;
import TypeAdapter.UpdatesTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;

public class Broadcaster extends Thread implements Constants {
    private List<Session> sessions;
    private Updates updates;
    private HashMap<Integer, Item> itemRespawn;
    private Map map;

    private class Item {
        int type;
        int time;

        Item(int type, int time) {
            this.type = type;
            this.time = time;
        }
    }

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

    public JsonObject sendReject() {
        JsonObject jsonObject = new JsonObject();
        JsonObject body = new JsonObject();
        body.add("warning", new JsonPrimitive("name already used"));
        jsonObject.add("type", new JsonPrimitive("Reject"));
        jsonObject.add("body", body);
        return jsonObject;
    }

    public void sendHit(String from, String to, int damage) {
        JsonObject jsonObject = new JsonObject();
        JsonObject body = new JsonObject();
        body.add("from", new JsonPrimitive(from));
        body.add("to", new JsonPrimitive(to));
        body.add("damage", new JsonPrimitive(damage));
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
            OutputStream os;
            int len = message.getBytes().length;
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream(len + 2);
                DataOutputStream dos = new DataOutputStream(bos);
                os = session.getOutputStream();
//                byte[] bytes = message.getBytes();
//                ByteBuffer buffer = ByteBuffer.allocate(2);
//                buffer.putShort((short) bytes.length);
//                System.out.println("buffer.array : " + Arrays.toString(buffer.array()));
//                System.out.println("message : " + message);
                dos.writeShort(len);
                dos.writeBytes(message);
                byte[] data = bos.toByteArray();
                System.out.println("len" + len);
                System.out.println(message);
//                buffer.flip();

                os.write(data);
//                os.write(buffer.array());
//                os.write(message.getBytes());
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
                    Item item = itemRespawn.get(index);
                    item.time += -1;
                    if (item.time < 0) {
                        map.getMap()[index] = item.type;
                        sendCorrectMap(index, item.type);
                        itemRespawn.remove(index);
                    }
                }

                for (Session session :
                        sessions) {
                    if (session.getUser() != null) {
                        moveUsers(session);
                    }
                }
            }
        }, 0, 10);

    }

    void update(List<Session> sessions) {
        this.sessions = sessions;
    }

    public void addItemRespawn(int index, int type) {
        itemRespawn.put(index, new Item(type, 1000));
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

    private void moveUsers(Session s) {
        if (s.getUser().getStamina() > 5 &&
                (s.getUser().getState().equals("MOVE") || s.getUser().getState().equals("SWIFT"))) {
            Update update = updates.getUpdates().get(s.getUser().getName());

            switch (s.getUser().getDirection()) {
                case "UP":
                    System.out.println("MOVE UP");
                    s.getUser().setY(s.getUser().getY() - s.getUser().getSpeed() / 10f);
                    update.setY(update.getY() - update.getSpeed() / 10f);
                    break;
                case "LEFT":
                    System.out.println("MOVE LEFT");
                    s.getUser().setX(s.getUser().getX() - s.getUser().getSpeed() / 10f);
                    update.setX(update.getX() - update.getSpeed() / 10f);
                    break;
                case "RIGHT":
                    System.out.println("MOVE RIGHT");
                    s.getUser().setX(s.getUser().getX() + s.getUser().getSpeed() / 10f);
                    update.setX(update.getX() + update.getSpeed() / 10f);
                    break;
                case "DOWN":
                    System.out.println("MOVE DOWN");
                    s.getUser().setY(s.getUser().getY() + s.getUser().getSpeed() / 10f);
                    update.setY(update.getY() + update.getSpeed() / 10f);
                    break;
            }

            int[] m = map.getMap();

            for (int i = 0; i < m.length; i++) {

                if (s.getUser().getX() < 0) {
                    s.getUser().setX(s.getUser().getX() + Constants.MAPSIZE);
                } else if (s.getUser().getX() > Constants.MAPSIZE) {
                    s.getUser().setX(s.getUser().getX() - Constants.MAPSIZE);
                }

                if (s.getUser().getY() < 0) {
                    s.getUser().setY(s.getUser().getY() + Constants.MAPSIZE);
                } else if (s.getUser().getY() > Constants.MAPSIZE) {
                    s.getUser().setY(s.getUser().getY() - Constants.MAPSIZE);
                }

                if (Util.getIndexByPos((int) s.getUser().getX(), (int) s.getUser().getY()) == i) {
                    switch (m[i]) {
                        case 1:
                            updates.getUpdates().get(s.getUser().getName()).setSpeed(PLAYER_SPEED_SLOW);
                            s.getUser().setSpeed(PLAYER_SPEED_SLOW);
//                            session.getUser().setSpeed(PLAYER_SPEED_SLOW);
                            break;

                        case 2:
                            if (s.getUser().getHp() >= FULL_HP - HEAL) {
                                updates.getUpdates().get(s.getUser().getName()).setHp(FULL_HP);
                                s.getUser().setHp(FULL_HP);
//                                session.getUser().setHp(FULL_HP);
                            } else if (s.getUser().getHp() <= FULL_HP - HEAL) {
                                updates.getUpdates().get(s.getUser().getName()).setHp(s.getUser().getHp() + HEAL);
                                s.getUser().setHp(s.getUser().getHp() + HEAL);
//                                session.getUser().setHp(s.getUser().getHp() + HEAL);
                            }
                            addItemRespawn(i, Constants.TILE_HEAL);
                            m[i] = 0;
                            sendCorrectMap(i, 0);
                            break;

                        case 3:
                            if (s.getUser().getMana() >= FULL_MANA - MANA) {
                                updates.getUpdates().get(s.getUser().getName()).setMana(FULL_MANA);
                                s.getUser().setMana(FULL_MANA);
                            } else if (s.getUser().getMana() <= FULL_MANA - MANA) {
                                updates.getUpdates().get(s.getUser().getName()).setMana(s.getUser().getMana() + MANA);
                                s.getUser().setMana(s.getUser().getMana() + MANA);
                            }
                            addItemRespawn(i, Constants.TILE_MANA);
                            m[i] = 0;
                            sendCorrectMap(i, 0);
                            break;

                        default:
                            if (s.getUser().getState().equals("SWIFT")) {
                                s.getUser().setSpeed(Constants.PLAYER_SPEED_SWIFT);
                                updates.getUpdates().get(s.getUser().getName()).setSpeed(PLAYER_SPEED_SWIFT);
                            }
                            if (s.getUser().getState().equals("MOVE")) {
                                s.getUser().setSpeed(Constants.PLAYER_SPEED);
                                updates.getUpdates().get(s.getUser().getName()).setSpeed(PLAYER_SPEED);
                            }
                            break;
                    }
                }
            }
        }
        useStamina(s);
        recoverStamina(s);
    }

    private void useStamina(Session s) {
        User user = s.getUser();
        Update update = updates.getUpdates().get(s.getUser().getName());
        if (user.getState().equals("SWIFT") && user.getStamina() > 1) {
            user.setStamina(user.getStamina() - 4);
            update.setStamina(update.getStamina() - 4);
        }
    }

    private void recoverStamina(Session s) {
        User user = s.getUser();
        Update update = updates.getUpdates().get(s.getUser().getName());
        if (user.getStamina() < 1000 &&
                (user.getState().equals("STOP") || user.getState().equals("MOVE"))) {
            user.setStamina(user.getStamina() + 2);
            update.setStamina(update.getStamina() + 2);
        }
    }
}