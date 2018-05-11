import Models.Join;
import Models.Map;
import Models.Move;
import Models.User;
import TypeAdapter.JoinTypeAdapter;
import TypeAdapter.MapTypeAdapter;
import TypeAdapter.MoveTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

interface SessionListener {
    void onJoin(Join join);

    void onDisconnect(Session session);

    void onMove(Session session, Move move);

//    void onAttack(Session)
}

public class Session extends Thread {
    private User user;
    private Socket socket;
    private SessionListener listener;
    private Broadcaster broadcaster;

    Session(Socket socket, Broadcaster broadcaster) {
        this.socket = socket;
        this.broadcaster = broadcaster;
        this.user = new User();
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
                System.out.println("str " + str);
                Gson gson = new GsonBuilder().create();
                JsonObject jsonObject = gson.fromJson(str, JsonObject.class);

//                String message = new String(buf, 0, len);
                String type = jsonObject.get("type").toString().replace("\"", "");
                String state = jsonObject.get("body").toString();

                switch (type) {
                    case "Join":
                        gson = new GsonBuilder()
                                .registerTypeAdapter(Join.class, new JoinTypeAdapter())
                                .create();
                        Join j = gson.fromJson(state, Join.class);
//                        System.out.println("join" + j);
                        listener.onJoin(j);
                        user.setName(j.getUser());
                        break;
                    case "Move":
                        gson = new GsonBuilder()
                                .registerTypeAdapter(Move.class, new MoveTypeAdapter())
                                .create();
                        Move m = gson.fromJson(state, Move.class);
                        listener.onMove(this, m);
                        break;
                    case "Attack":

                        break;
//                        gson = new GsonBuilder()
//                                .registerTypeAdapter(Move.class, new MoveTypeAdapter())
//                                .create();
//                        Move m = gson.fromJson(state, Move.class);
//                        listener.onMove(this, m);
//                        break;
                }
            }

            if (listener != null)
                listener.onDisconnect(this);

            System.out.println("Connection Thread end");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void disconnect() {
//
//    }


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

    public User getUser() {
        return user;
    }
}
