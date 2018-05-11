
import Models.Map;
import Models.Updates;
import TypeAdapter.MapTypeAdapter;
import TypeAdapter.UpdatesTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Broadcaster extends Thread {
    private List<Session> sessions;
    private Updates updates;

    Broadcaster(List<Session> sessions, Updates updates) {
        this.sessions = sessions;
        this.updates = updates;
    }

    public void sendPosition(String message) {
        broadcast(message);
    }

    public void sendUpdates() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Updates.class, new UpdatesTypeAdapter())
                .create();
        broadcast(gson.toJson(updates));
//        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
//        byteBuffer.putShort((short) gson.toJson(map).getBytes().length);
//        try {
//            socket.getOutputStream().write(byteBuffer.array());
//            socket.getOutputStream().write(gson.toJson(map).getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
            }  catch (IOException e ) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
                // TODO...
                sendUpdates();
//                int index = random.nextInt(words.size());


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
