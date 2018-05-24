import Models.*;
import TypeAdapter.ImageTypeAdapter;
import TypeAdapter.JoinTypeAdapter;
import TypeAdapter.MapTypeAdapter;
import TypeAdapter.MoveTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

import static Models.Constants.MAPSIZE;
import static Models.Constants.USER_MOVE;
import static Models.Constants.USER_STOP;

interface SessionListener {
    void onJoin(User user);

    void onDisconnect(Session session);

    void onMove(Session session, Move move);

    void onAttack(Session session);

    void onSpecial(Session session);

    void onStop(Session session);

    void onSetImage(Session session, Image image);

    void onSwift(Session session);
}

public class Session extends Thread {
    private User user;
    private Socket socket;
    private SessionListener listener;

    Session(Socket socket) {
        this.socket = socket;
//        this.user = new User();
    }

    void setSessionListener(SessionListener listener) {
        this.listener = listener;
    }

    OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    @Override
        public void run() {
            int len;
            byte[] lengthBuf = new byte[2];
            byte[] buf = new byte[3000];
            ByteBuffer byteBuffer = ByteBuffer.allocate(2);

            try {
                while ((len = socket.getInputStream().read(lengthBuf, 0, 2)) != -1) {
                    byteBuffer.put(lengthBuf[0]);
                    byteBuffer.put(lengthBuf[1]);
                    byteBuffer.flip();
                    Short length = byteBuffer.getShort();
                    byteBuffer.clear();

                    len = socket.getInputStream().read(buf, 0, length);
                    String str = new String(buf, 0, len);
//                    System.out.println("str " + str);
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(str, JsonObject.class);

    //                String message = new String(buf, 0, len);
                    String type = jsonObject.get("type").toString().replace("\"", "");
                    String state;

                    switch (type) {
                        case "Join":
                            state = jsonObject.get("body").toString();
                            gson = new GsonBuilder()
                                    .registerTypeAdapter(Join.class, new JoinTypeAdapter())
                                    .create();
                            Join j = gson.fromJson(state, Join.class);
                            user = new User((float) (Math.random() * MAPSIZE), (float) (Math.random() * MAPSIZE), j.getUser(),
                                    Constants.PLAYER_DOWN, 100, 100,1000, 0, USER_STOP);
                            listener.onJoin(user);
                            break;
                        case "Move":
                            state = jsonObject.get("body").toString();
                            gson = new GsonBuilder()
                                    .registerTypeAdapter(Move.class, new MoveTypeAdapter())
                                    .create();
                            Move m = gson.fromJson(state, Move.class);
                            listener.onMove(this, m);
                            break;
                        case "Attack":
                            user.setState("ATTACK");
                            listener.onAttack(this);
                            break;
                        case "Special":
                            user.setState("SPECIAL");
                            listener.onSpecial(this);
                            break;
                        case "Stop":
                            user.setState("STOP");
                            listener.onStop(this);
                            break;
                        case "Character" :
                            state = jsonObject.get("body").toString();
                            gson = new GsonBuilder()
                                    .registerTypeAdapter(Image.class, new ImageTypeAdapter())
                                    .create();
                            Image image = gson.fromJson(state, Image.class);
                            user.setCharacterImage(image.getCharacterImage());
                            System.out.println("character : " + image.getCharacterImage());
                            listener.onSetImage(this, image);
                            break;
                        case "SWIFT":
                            user.setState("SWIFT");
                            listener.onSwift(this);
                            break;
                    }
                }

                if (listener != null)
                    listener.onDisconnect(this);

                System.out.println("Connection Thread end");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void send(JsonObject jsonObject) {
        Gson gson = new GsonBuilder().create();

        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.putShort((short) gson.toJson(jsonObject).getBytes().length);
        try {
            socket.getOutputStream().write(byteBuffer.array());
            socket.getOutputStream().write(gson.toJson(jsonObject).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMap(Map map) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Map.class, new MapTypeAdapter())
                .create();
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.putShort((short) gson.toJson(map).getBytes().length);
        try {
            socket.getOutputStream().write(byteBuffer.array());
            socket.getOutputStream().write(gson.toJson(map).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
